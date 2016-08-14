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

/*import com.aheidelbacher.algostorm.engine.Engine
import com.aheidelbacher.algostorm.engine.graphics2d.Canvas
import com.aheidelbacher.algostorm.engine.graphics2d.RenderingSystem
import com.aheidelbacher.algostorm.engine.input.InputReader
import com.aheidelbacher.algostorm.engine.logging.LoggingSystem
import com.aheidelbacher.algostorm.engine.logging.SystemLogger
import com.aheidelbacher.algostorm.engine.physics2d.PhysicsSystem
import com.aheidelbacher.algostorm.engine.script.ScriptEngine
import com.aheidelbacher.algostorm.engine.script.ScriptingSystem
import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.EventBus
import com.aheidelbacher.algostorm.event.Subscriber
import com.aheidelbacher.algostorm.event.Subscription
import com.aheidelbacher.algoventure.core.act.ActingSystem
import com.aheidelbacher.algoventure.core.attack.AttackSystem
import com.aheidelbacher.algoventure.core.damage.DamageSystem
import com.aheidelbacher.algoventure.core.facing.FacingSystem
import com.aheidelbacher.algoventure.core.input.Input
import com.aheidelbacher.algoventure.core.input.InputSystem
import com.aheidelbacher.algoventure.core.move.MovementSystem
import com.aheidelbacher.algoventure.core.state.State.playerObjectId

class StateManager(
        private val map: Map,
        private val eventBus: EventBus,
        canvas: Canvas,
        private val inputReader: InputReader<Input>,
        private val scriptEngine: ScriptEngine,
        private val levelCount: Int
) {
    private val globalSubscriptions = listOf(
        LoggingSystem(SystemLogger()),
        RenderingSystem(map, canvas),
        ScriptingSystem(
                scriptEngine = scriptEngine,
                scripts = Serializer.readValue<List<String>>(
                        Engine.getResource("/scripts.json")
                ).map { Engine.getResource(it) }
        )
    ).map { eventBus.subscribe(it) }
    private var levelSubscriptions = emptyList<Subscription>()

    fun initLevel(level: Int) {
        levelSubscriptions.forEach { it.unsubscribe() }
        val objectManager = ObjectManager(map, "objects")
        levelSubscriptions = listOf(
                PhysicsSystem(objectManager, eventBus),
                MovementSystem(
                        tileWidth = map.tileWidth,
                        tileHeight = map.tileHeight,
                        objectManager = objectManager,
                        publisher = eventBus
                ),
                FacingSystem(objectManager),
                ActingSystem(objectManager, eventBus, scriptEngine),
                InputSystem(
                        map = map,
                        objectManager = objectManager,
                        objectId = map.playerObjectId,
                        inputReader = inputReader
                ),
                DamageSystem(objectManager, eventBus),
                AttackSystem(
                        tileWidth = map.tileWidth,
                        tileHeight = map.tileHeight,
                        objectManager = objectManager,
                        publisher = eventBus
                )
        ).map { eventBus.subscribe(it) }
    }

    fun clearState() {
        levelSubscriptions.forEach { it.unsubscribe() }
        globalSubscriptions.forEach { it.unsubscribe() }
        eventBus.publishPosts()
    }
}*/
