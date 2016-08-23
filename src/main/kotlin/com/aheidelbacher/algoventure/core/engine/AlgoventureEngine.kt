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
import com.aheidelbacher.algostorm.engine.Update
import com.aheidelbacher.algostorm.engine.graphics2d.RenderingSystem.Render
import com.aheidelbacher.algostorm.engine.graphics2d.RenderingSystem
import com.aheidelbacher.algostorm.engine.graphics2d.camera.Camera.Companion.getCamera
import com.aheidelbacher.algostorm.engine.graphics2d.camera.CameraSystem
import com.aheidelbacher.algostorm.engine.input.AbstractInputSystem.HandleInput
import com.aheidelbacher.algostorm.engine.log.LoggingSystem
import com.aheidelbacher.algostorm.engine.physics2d.PhysicsSystem
import com.aheidelbacher.algostorm.engine.script.JavascriptEngine
import com.aheidelbacher.algostorm.engine.script.ScriptingSystem
import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algostorm.engine.sound.SoundSystem
import com.aheidelbacher.algostorm.engine.sound.SoundSystem.PlayMusic
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.EventQueue

import com.aheidelbacher.algoventure.core.act.ActingSystem
import com.aheidelbacher.algoventure.core.act.ActingSystem.NewAct
import com.aheidelbacher.algoventure.core.attack.AttackSystem
import com.aheidelbacher.algoventure.core.damage.DamageSystem
import com.aheidelbacher.algoventure.core.event.ObjectEventHandlingSystem
import com.aheidelbacher.algoventure.core.facing.FacingSystem
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonMapGenerator
import com.aheidelbacher.algoventure.core.graphics2d.RenderOrderSystem
import com.aheidelbacher.algoventure.core.input.InputSystem
import com.aheidelbacher.algoventure.core.log.EventSystemLogger
import com.aheidelbacher.algoventure.core.move.MovementSystem
import com.aheidelbacher.algoventure.core.state.State
import com.aheidelbacher.algoventure.core.state.State.objectGroup
import com.aheidelbacher.algoventure.core.state.State.playerObjectId
import com.aheidelbacher.algoventure.core.ui.UiSystem

import java.io.InputStream
import java.io.OutputStream

class AlgoventureEngine private constructor(
        private val map: Map,
        platform: Platform
) : Engine() {
    constructor(
            inputStream: InputStream,
            platform: Platform
    ) : this(
            map = Serializer.readValue<Map>(inputStream),
            platform = platform
    )

    constructor(playerObjectType: String, platform: Platform) : this(
            map = DungeonMapGenerator.newMap(playerObjectType),
            platform = platform
    )

    private val eventBus = EventQueue()
    private val objectManager = ObjectManager(map, State.OBJECT_GROUP_NAME)
    private val scriptEngine = JavascriptEngine { getResourceStream(it) }
    private val canvas = platform.canvas
    private val soundEngine = platform.soundEngine
    private val camera = map.getCamera()
    private val subscriptions = listOf(
            LoggingSystem(EventSystemLogger()),
            RenderingSystem(map, canvas),
            RenderOrderSystem(map.objectGroup, eventBus),
            CameraSystem(camera, objectManager, eventBus, map.playerObjectId),
            SoundSystem(
                    soundEngine = soundEngine,
                    musicSounds = Serializer.readValue<List<String>>(
                            getResourceStream("/musicSounds.json")
                    ),
                    sounds = Serializer.readValue<List<String>>(
                            getResourceStream("/sounds.json")
                    )
            ),
            UiSystem(
                    uiHandler = platform.uiHandler,
                    objectManager = objectManager,
                    objectId = map.playerObjectId,
                    publisher = eventBus
            ),
            PhysicsSystem(objectManager, eventBus),
            MovementSystem(
                    tileWidth = map.tileWidth,
                    tileHeight = map.tileHeight,
                    objectManager = objectManager,
                    publisher = eventBus
            ),
            FacingSystem(objectManager),
            ScriptingSystem(
                    scriptEngine = scriptEngine,
                    scripts = Serializer.readValue<List<String>>(
                            getResourceStream("/scripts.json")
                    )
            ),
            ObjectEventHandlingSystem(objectManager, eventBus),
            ActingSystem(objectManager, eventBus),
            InputSystem(
                    tileWidth = map.tileWidth,
                    tileHeight = map.tileHeight,
                    objectManager = objectManager,
                    publisher = eventBus,
                    objectId = map.playerObjectId,
                    camera = camera,
                    inputReader = platform.inputReader
            ),
            DamageSystem(objectManager, eventBus),
            AttackSystem(map.tileWidth, map.tileHeight, objectManager, eventBus)
    ).map { eventBus.subscribe(it) }

    private val playerObject: Object?
        get() = objectManager[map.playerObjectId]

    override val millisPerUpdate: Int
        get() = 25

    private val isIdle: Boolean
        get() = true

    init {
        eventBus.publish(PlayMusic("/sounds/game_soundtrack.mp3", true))
    }

    override fun onHandleInput() {
        eventBus.post(HandleInput)
        eventBus.publishPosts()
    }

    override fun onUpdate() {
        if (playerObject != null) {
            eventBus.post(NewAct)
            eventBus.publishPosts()
        }
        eventBus.post(Update(millisPerUpdate))
        eventBus.publishPosts()
    }

    override fun onRender() {
        eventBus.post(Render(camera.x, camera.y))
        eventBus.publishPosts()
    }

    override fun writeStateToStream(outputStream: OutputStream) {
        while (!isIdle) {
            onUpdate()
        }
        Serializer.writeValue(outputStream, map)
    }

    override fun clearState() {
        canvas.unloadBitmaps()
        soundEngine.release()
        subscriptions.forEach { it.unsubscribe() }
        objectManager.objects.toList().forEach {
            it.properties.clear()
            objectManager.delete(it.id)
        }
        map.properties.clear()
        map.layers.forEach { it.properties.clear() }
    }
}
