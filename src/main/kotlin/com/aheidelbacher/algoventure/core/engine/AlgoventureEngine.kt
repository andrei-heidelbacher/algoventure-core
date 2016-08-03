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
import com.aheidelbacher.algostorm.event.EventBus
import com.aheidelbacher.algostorm.graphics2d.Render
import com.aheidelbacher.algostorm.graphics2d.RenderingSystem
import com.aheidelbacher.algostorm.physics2d.PhysicsSystem
import com.aheidelbacher.algostorm.serialization.Serializer
import com.aheidelbacher.algostorm.state.Map
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.state.ObjectManager
import com.aheidelbacher.algostorm.time.Tick
import com.aheidelbacher.algoventure.core.act.ActingSystem

import com.aheidelbacher.algoventure.core.facing.FacingSystem
import com.aheidelbacher.algoventure.core.input.InputSystem
import com.aheidelbacher.algoventure.core.move.MovementSystem
import com.aheidelbacher.algoventure.core.script.JavascriptEngine
import com.aheidelbacher.algoventure.core.script.ScriptingSystem

import java.io.InputStreamReader
import java.io.OutputStream

class AlgoventureEngine(
        private val map: Map,
        private val eventBus: EventBus,
        platform: Platform
) : Engine() {
    companion object {
        const val FLOOR_TILE_LAYER: String = "floor"
        const val OBJECT_GROUP_NAME: String = "objects"
        const val PLAYER_OBJECT_ID_PROPERTY: String = "playerId"
    }

    private val objectManager = ObjectManager(map, OBJECT_GROUP_NAME)
    private val scriptingEngine = JavascriptEngine(listOf(InputStreamReader(
            this.javaClass.getResourceAsStream("/player_input.js")
    )))
    private val systems = listOf(
            RenderingSystem(map, platform.canvas),
            PhysicsSystem(objectManager, eventBus),
            MovementSystem(objectManager, eventBus),
            FacingSystem(objectManager),
            ScriptingSystem(scriptingEngine),
            ActingSystem(objectManager, eventBus, scriptingEngine),
            InputSystem(
                    objectManager = objectManager,
                    objectId = map.properties[PLAYER_OBJECT_ID_PROPERTY] as Int?
                            ?: error("Missing player id property!"),
                    inputReader = platform.inputReader
            )
    )
    private val subscriptions = systems.map { eventBus.subscribe(it) }

    private fun getPlayer(): Object? =
            map.properties[PLAYER_OBJECT_ID_PROPERTY]?.let {
                objectManager[it as Int]
            }

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
        eventBus.post(Tick(millisPerTick))
        eventBus.publishPosts()
        val playerObj = getPlayer()
        if (playerObj != null) {
            eventBus.post(Render(
                    cameraX = playerObj.x + playerObj.width / 2,
                    cameraY = playerObj.y + playerObj.height / 2
            ))
            eventBus.publishPosts()
        }
    }

    override fun writeStateToStream(outputStream: OutputStream) {
        Serializer.writeValue(outputStream, map)
    }
}
