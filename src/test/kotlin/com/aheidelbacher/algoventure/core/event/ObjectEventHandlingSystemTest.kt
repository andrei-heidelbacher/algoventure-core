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

package com.aheidelbacher.algoventure.core.event

import com.aheidelbacher.algostorm.engine.Engine.Companion.getResourceStream
import com.aheidelbacher.algostorm.engine.log.Logger
import com.aheidelbacher.algostorm.engine.log.LoggingSystem
import com.aheidelbacher.algostorm.engine.script.JavascriptEngine
import com.aheidelbacher.algostorm.engine.script.ScriptingSystem
import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.EventQueue

import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonMapGenerator
import com.aheidelbacher.algoventure.core.geometry2d.Direction
import com.aheidelbacher.algoventure.core.move.Moved
import com.aheidelbacher.algoventure.core.state.State.playerObjectId

import org.junit.Before
import org.junit.Test

class ObjectEventHandlingSystemTest {
    private val map = DungeonMapGenerator.newMap("knight")
    private val objectManager = ObjectManager(map, "objects")
    private val eventBus = EventQueue()
    private val loggingSystem = LoggingSystem(Logger {
        when (it) {
            is Moved -> true
            is ScriptingSystem.RunScript -> true
            else -> false
        }
    })
    private val hookSystem = ObjectEventHandlingSystem(objectManager, eventBus)
    private val scriptingSystem = ScriptingSystem(
            scriptEngine = JavascriptEngine { getResourceStream(it) },
            scripts = Serializer.readValue<List<String>>(
                    getResourceStream("/scripts.json")
            )
    )

    @Before
    fun setup() {
        eventBus.subscribe(loggingSystem)
        eventBus.subscribe(scriptingSystem)
        eventBus.subscribe(hookSystem)
    }

    @Test
    fun testDoorSwitchHook() {
        eventBus.post(Moved(map.playerObjectId, Direction.WEST, 0))
        eventBus.publishPosts()
    }
}
