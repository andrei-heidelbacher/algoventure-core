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
import com.aheidelbacher.algostorm.event.EventQueue
import com.aheidelbacher.algostorm.graphics2d.Render
import com.aheidelbacher.algostorm.graphics2d.RenderingSystem
import com.aheidelbacher.algostorm.input.HandleInput
import com.aheidelbacher.algostorm.physics2d.PhysicsSystem
import com.aheidelbacher.algostorm.script.ScriptingSystem
import com.aheidelbacher.algostorm.serialization.Serializer
import com.aheidelbacher.algostorm.state.Map
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.state.ObjectManager
import com.aheidelbacher.algostorm.time.Tick
import com.aheidelbacher.algoventure.core.act.ActingSystem
import com.aheidelbacher.algoventure.core.act.NewAct

import com.aheidelbacher.algoventure.core.facing.FacingSystem
import com.aheidelbacher.algoventure.core.input.InputSystem
import com.aheidelbacher.algoventure.core.move.MovementSystem
import com.aheidelbacher.algoventure.core.script.JavascriptEngine
import com.aheidelbacher.algoventure.core.state.State
import com.aheidelbacher.algoventure.core.state.State.cameraX
import com.aheidelbacher.algoventure.core.state.State.cameraY
import com.aheidelbacher.algoventure.core.state.State.playerObjectId

import java.io.OutputStream

class AlgoventureEngine(
        private val map: Map,
        platform: Platform
) : Engine() {
    private val eventBus = EventQueue()
    private val objectManager = ObjectManager(map, State.OBJECT_GROUP_NAME)
    private val scriptEngine = JavascriptEngine()
    private val scripts =
            listOf(this.javaClass.getResourceAsStream("/player_input.js"))
    private val systems = listOf(
            RenderingSystem(map, platform.canvas),
            PhysicsSystem(objectManager, eventBus),
            MovementSystem(objectManager, eventBus),
            FacingSystem(objectManager),
            ScriptingSystem(
                    scriptEngine = scriptEngine,
                    scripts = scripts
            ),
            ActingSystem(objectManager, eventBus, scriptEngine),
            InputSystem(
                    map = map,
                    objectManager = objectManager,
                    objectId = map.playerObjectId,
                    inputReader = platform.inputReader
            )
    )
    private val subscriptions = systems.map { eventBus.subscribe(it) }

    private val playerObject: Object?
        get() = objectManager[map.playerObjectId]

    override val millisPerTick: Int
        get() = 15

    override fun clearState() {
        subscriptions.forEach { it.unsubscribe() }
        objectManager.objects.toList().forEach {
            it.properties.clear()
            objectManager.delete(it.id)
        }
    }

    override fun handleTick() {
        playerObject?.let { playerObj ->
            eventBus.post(HandleInput, NewAct(playerObj.id))
            eventBus.publishPosts()
            val cameraX = playerObj.x + playerObj.width / 2
            val cameraY = playerObj.y + playerObj.height / 2
            map.cameraX = cameraX
            map.cameraY = cameraY
            eventBus.post(Render(cameraX, cameraY))
            eventBus.publishPosts()
        }
        eventBus.post(Tick(millisPerTick))
        eventBus.publishPosts()
    }

    override fun writeStateToStream(outputStream: OutputStream) {
        Serializer.writeValue(outputStream, map)
    }
}
