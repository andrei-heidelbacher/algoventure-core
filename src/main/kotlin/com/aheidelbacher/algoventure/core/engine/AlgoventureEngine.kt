/*
 * Copyright 2016 Andrei Heidelbacher <andrei.heidelbacher@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aheidelbacher.algoventure.core.engine

import com.aheidelbacher.algostorm.engine.Engine
import com.aheidelbacher.algostorm.systems.Update
import com.aheidelbacher.algostorm.engine.audio.AudioDriver
import com.aheidelbacher.algostorm.engine.graphics2d.GraphicsDriver
import com.aheidelbacher.algostorm.systems.graphics2d.RenderingSystem.Render
import com.aheidelbacher.algostorm.systems.graphics2d.RenderingSystem
import com.aheidelbacher.algostorm.systems.graphics2d.camera.Camera.Companion.getCamera
import com.aheidelbacher.algostorm.systems.graphics2d.camera.CameraSystem
import com.aheidelbacher.algostorm.systems.input.AbstractInputSystem.HandleInput
import com.aheidelbacher.algostorm.engine.input.InputDriver
import com.aheidelbacher.algostorm.systems.log.Logger
import com.aheidelbacher.algostorm.systems.log.LoggingSystem
import com.aheidelbacher.algostorm.systems.physics2d.PhysicsSystem
import com.aheidelbacher.algostorm.engine.script.JavascriptDriver
import com.aheidelbacher.algostorm.engine.serialization.Deserializer.Companion.readValue
import com.aheidelbacher.algostorm.systems.script.ScriptingSystem
import com.aheidelbacher.algostorm.systems.audio.MusicSystem
import com.aheidelbacher.algostorm.systems.audio.MusicSystem.PlayMusic
import com.aheidelbacher.algostorm.systems.audio.SoundSystem
import com.aheidelbacher.algostorm.state.File
import com.aheidelbacher.algostorm.state.MapObject
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.event.EventQueue

import com.aheidelbacher.algoventure.core.act.ActingSystem
import com.aheidelbacher.algoventure.core.act.ActingSystem.NewAct
import com.aheidelbacher.algoventure.core.attack.AttackSystem
import com.aheidelbacher.algoventure.core.damage.DamageSystem
import com.aheidelbacher.algoventure.core.damage.HealthBarSystem
import com.aheidelbacher.algoventure.core.event.ObjectEventHandlingSystem
import com.aheidelbacher.algoventure.core.facing.FacingSystem
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonMapGenerator
import com.aheidelbacher.algoventure.core.graphics2d.RenderOrderSystem
import com.aheidelbacher.algoventure.core.input.InputSystem
import com.aheidelbacher.algoventure.core.move.MovementSystem
import com.aheidelbacher.algoventure.core.serialization.JsonSerializer
import com.aheidelbacher.algoventure.core.state.healthBars
import com.aheidelbacher.algoventure.core.state.isValid
import com.aheidelbacher.algoventure.core.state.objectGroup
import com.aheidelbacher.algoventure.core.state.playerObjectId
import com.aheidelbacher.algoventure.core.ui.UiHandler
import com.aheidelbacher.algoventure.core.ui.UiSystem

import java.io.InputStream
import java.io.OutputStream

class AlgoventureEngine private constructor(
        audioDriver: AudioDriver,
        graphicsDriver: GraphicsDriver,
        inputDriver: InputDriver,
        uiHandler: UiHandler,
        private val map: MapObject
) : Engine(
        audioDriver = audioDriver,
        graphicsDriver = graphicsDriver,
        inputDriver = inputDriver,
        scriptDriver = JavascriptDriver { getResourceStream(it) },
        serializationDriver = JsonSerializer
) {
    constructor(
            audioDriver: AudioDriver,
            graphicsDriver: GraphicsDriver,
            inputDriver: InputDriver,
            uiHandler: UiHandler,
            inputStream: InputStream
    ) : this(
            audioDriver = audioDriver,
            graphicsDriver = graphicsDriver,
            inputDriver = inputDriver,
            uiHandler = uiHandler,
            map = JsonSerializer.readValue<MapObject>(inputStream)
    )

    constructor(
            audioDriver: AudioDriver,
            graphicsDriver: GraphicsDriver,
            inputDriver: InputDriver,
            uiHandler: UiHandler,
            playerObjectType: String
    ) : this(
            audioDriver = audioDriver,
            graphicsDriver = graphicsDriver,
            inputDriver = inputDriver,
            uiHandler = uiHandler,
            map = DungeonMapGenerator.newMap(playerObjectType)
    )

    private val eventBus = EventQueue()
    private val objectGroup = map.objectGroup
    private val camera = map.getCamera()
    private val systems = listOf(
            LoggingSystem(Logger {
                when (it) {
                    is HandleInput -> false
                    is Update -> false
                    is CameraSystem.UpdateCamera -> false
                    is RenderOrderSystem.SortObjects -> false
                    is Render -> false
                    is NewAct -> false
                    is HealthBarSystem.UpdateHealthBars -> false
                    is ScriptingSystem.RunScriptWithResult ->
                        it.functionName != "getPlayerInput"
                    else -> true
                }
            }),
            RenderingSystem(map, graphicsDriver),
            RenderOrderSystem(map.objectGroup, eventBus),
            CameraSystem(camera, objectGroup, eventBus, map.playerObjectId),
            MusicSystem(
                    musicPlayer = audioDriver,
                    musicSources = serializationDriver.readValue<List<String>>(
                            getResourceStream("/musicSounds.json")
                    ).map(::File)
            ),
            SoundSystem(
                    soundPlayer = audioDriver,
                    soundSources = serializationDriver.readValue<List<String>>(
                            getResourceStream("/sounds.json")
                    ).map(::File)
            ),
            UiSystem(
                    uiHandler = uiHandler,
                    objectGroup = objectGroup,
                    objectId = map.playerObjectId,
                    publisher = eventBus
            ),
            HealthBarSystem(map, objectGroup, map.healthBars, eventBus),
            PhysicsSystem(objectGroup, eventBus),
            MovementSystem(
                    tileWidth = map.tileWidth,
                    tileHeight = map.tileHeight,
                    objectGroup = objectGroup,
                    publisher = eventBus
            ),
            FacingSystem(objectGroup),
            ScriptingSystem(
                    scriptEngine = scriptDriver,
                    scriptSources = serializationDriver.readValue<List<String>>(
                            getResourceStream("/scripts.json")
                    ).map(::File)
            ),
            ObjectEventHandlingSystem(objectGroup, eventBus),
            ActingSystem(objectGroup, eventBus),
            InputSystem(
                    inputSource = inputDriver,
                    tileWidth = map.tileWidth,
                    tileHeight = map.tileHeight,
                    objectGroup = objectGroup,
                    publisher = eventBus,
                    objectId = map.playerObjectId,
                    camera = camera
            ),
            DamageSystem(objectGroup, eventBus),
            AttackSystem(map.tileWidth, map.tileHeight, objectGroup, eventBus)
    ).apply { forEach { system -> eventBus.subscribe(system) } }

    private val playerObject: Object?
        get() = objectGroup[map.playerObjectId]

    private val isIdle: Boolean
        get() = true

    override val millisPerUpdate: Int
        get() = 25

    private var skipRender = false

    init {
        require(map.isValid) { "Invalid map generated!" }
        eventBus.publish(PlayMusic(File("/sounds/game_soundtrack.mp3"), true))
    }

    override fun onRender() {
        if (!skipRender) {
            graphicsDriver.lockCanvas()
            eventBus.publish(Render(camera.x, camera.y))
            graphicsDriver.unlockAndPostCanvas()
            skipRender = true
        } else {
            skipRender = false
        }
    }

    override fun onHandleInput() {
        eventBus.publish(HandleInput)
    }

    override fun onUpdate() {
        if (playerObject != null) {
            eventBus.post(NewAct)
            eventBus.publishPosts()
        }
        eventBus.post(Update(millisPerUpdate))
        eventBus.publishPosts()
    }

    override fun onStart() {
        audioDriver.resumeMusic()
    }

    override fun onSerializeState(outputStream: OutputStream) {
        while (!isIdle) {
            onUpdate()
        }
        serializationDriver.writeValue(outputStream, map)
    }

    override fun onStop() {
        audioDriver.stopMusic()
    }

    override fun onShutdown() {
        systems.forEach { eventBus.unsubscribe(it) }
        objectGroup.objectSet.forEach { it.properties.clear() }
        objectGroup.clear()
        map.properties.clear()
        map.layers.forEach { it.properties.clear() }
    }
}
