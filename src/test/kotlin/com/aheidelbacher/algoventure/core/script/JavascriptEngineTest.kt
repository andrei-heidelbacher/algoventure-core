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

package com.aheidelbacher.algoventure.core.script

import com.aheidelbacher.algoventure.core.act.Action
import com.aheidelbacher.algostorm.engine.script.ScriptEngine.Companion.invokeFunction
import com.aheidelbacher.algostorm.engine.state.Layer
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

import kotlin.concurrent.thread

class JavascriptEngineTest {
    companion object {
        private const val NAME = "objects"
        private val MAP = Map(
                width = 32,
                height = 32,
                tileWidth = 24,
                tileHeight = 24,
                orientation = Map.Orientation.ORTHOGONAL,
                tileSets = emptyList(),
                layers = listOf(
                        Layer.ObjectGroup(
                                NAME,
                                hashSetOf(
                                   Object(1, 0, 0, 24, 24)
                                )
                        )
                ),
                nextObjectId = 2
        )
        private val OBJECT_MANAGER = ObjectManager(MAP, NAME)
        private val ACTOR_ID = 1
    }

    private val playerScript = this.javaClass.getResourceAsStream(
            "/scripts/get_player_input.js"
    )
    private val drunkScript = this.javaClass.getResourceAsStream(
            "/scripts/drunk_walk_ai.js"
    )
    private val engine = JavascriptEngine().apply {
        eval(playerScript)
        eval(drunkScript)
    }

    @Test
    fun testPlayerInputScript() {
        val result = engine.invokeFunction<Action>(
                "getPlayerInput",
                OBJECT_MANAGER,
                ACTOR_ID
        )
        assertEquals(null, result)
    }

    @Test
    fun testPlayerInputScriptReturn() {
        val action = Action.Wait(ACTOR_ID)
        OBJECT_MANAGER[ACTOR_ID]?.properties?.put("lastInput", action)
        val result = engine.invokeFunction<Action>(
                "getPlayerInput",
                OBJECT_MANAGER,
                ACTOR_ID
        )
        OBJECT_MANAGER[ACTOR_ID]?.properties?.remove("lastInput")
        assertEquals(action, result)
    }

    @Test
    fun testPlayerInputScriptAsync() {
        thread { testPlayerInputScript() }.join()
        thread { testPlayerInputScriptReturn() }.join()
    }

    @Test
    fun testDrunkWalkScript() {
        val result = engine.invokeFunction<Action>(
                "getDrunkWalkInput",
                OBJECT_MANAGER,
                ACTOR_ID
        )
        println(result)
        assertNotNull(result)
    }
}
