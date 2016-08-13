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
import com.aheidelbacher.algostorm.engine.graphics2d.Render
import com.aheidelbacher.algostorm.engine.graphics2d.RenderingSystem
import com.aheidelbacher.algostorm.engine.input.HandleInput
import com.aheidelbacher.algostorm.engine.logging.LoggingSystem
import com.aheidelbacher.algostorm.engine.logging.SystemLogger
import com.aheidelbacher.algostorm.engine.physics2d.PhysicsSystem
import com.aheidelbacher.algostorm.engine.script.ScriptingSystem
import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.engine.time.Tick
import com.aheidelbacher.algostorm.event.EventQueue

import com.aheidelbacher.algoventure.core.act.ActingSystem
import com.aheidelbacher.algoventure.core.act.Actor.isActor
import com.aheidelbacher.algoventure.core.act.NewAct
import com.aheidelbacher.algoventure.core.attack.AttackSystem
import com.aheidelbacher.algoventure.core.damage.DamageSystem
import com.aheidelbacher.algoventure.core.facing.FacingSystem
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonMapGenerator
import com.aheidelbacher.algoventure.core.input.InputSystem
import com.aheidelbacher.algoventure.core.move.MovementSystem
import com.aheidelbacher.algoventure.core.script.JavascriptEngine
import com.aheidelbacher.algoventure.core.state.State
import com.aheidelbacher.algoventure.core.state.State.cameraX
import com.aheidelbacher.algoventure.core.state.State.cameraY
import com.aheidelbacher.algoventure.core.state.State.playerObjectId

import java.io.InputStream
import java.io.OutputStream

class AlgoventureEngine private constructor(
        private val map: Map,
        platform: Platform
) : Engine() {
    constructor(inputStream: InputStream, platform: Platform) : this(
            map = Serializer.readValue<Map>(inputStream),
            platform = platform
    )

    constructor(playerPrototype: String, platform: Platform) : this(
            map = DungeonMapGenerator.newMap(playerPrototype),
            platform = platform
    )

    private val eventBus = EventQueue()
    private val objectManager = ObjectManager(map, State.OBJECT_GROUP_NAME)
    private val scriptEngine = JavascriptEngine()
    private val systems = listOf(
            LoggingSystem(SystemLogger()),
            RenderingSystem(
                    map = map,
                    canvas = platform.canvas
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
                            Engine.getResource("/scripts.json")
                    ).map { Engine.getResource(it) }
            ),
            ActingSystem(objectManager, eventBus, scriptEngine),
            InputSystem(
                    map = map,
                    objectManager = objectManager,
                    objectId = map.playerObjectId,
                    inputReader = platform.inputReader
            ),
            DamageSystem(objectManager, eventBus),
            AttackSystem(map.tileWidth, map.tileHeight, objectManager, eventBus)
    )
    private val subscriptions = systems.map { eventBus.subscribe(it) }

    private val playerObject: Object?
        get() = objectManager[map.playerObjectId]

    override val millisPerTick: Int
        get() = 25

    private val isIdle: Boolean
        get() = true

    override fun clearState() {
        subscriptions.forEach { it.unsubscribe() }
        objectManager.objects.toList().forEach {
            it.properties.clear()
            objectManager.delete(it.id)
        }
        map.properties.clear()
        map.layers.forEach { it.properties.clear() }
    }

    override fun handleTick() {
        playerObject?.let { playerObj ->
            eventBus.post(HandleInput)
            eventBus.publishPosts()
            repeat(objectManager.objects.count { it.isActor }) {
                eventBus.post(NewAct)
                eventBus.publishPosts()
            }
            val cameraX = playerObj.x + playerObj.width / 2
            val cameraY = playerObj.y + playerObj.height / 2
            map.cameraX = cameraX
            map.cameraY = cameraY
        }
        eventBus.post(Render(map.cameraX, map.cameraY))
        eventBus.publishPosts()
        eventBus.post(Tick(millisPerTick))
        eventBus.publishPosts()
    }

    override fun writeStateToStream(outputStream: OutputStream) {
        while (!isIdle) {
            handleTick()
        }
        Serializer.writeValue(outputStream, map)
    }
}
