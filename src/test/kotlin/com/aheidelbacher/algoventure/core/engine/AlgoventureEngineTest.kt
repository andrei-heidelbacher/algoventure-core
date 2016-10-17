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
import com.aheidelbacher.algostorm.test.engine.EngineTest
import com.aheidelbacher.algostorm.test.engine.audio.AudioDriverMock
import com.aheidelbacher.algostorm.test.engine.graphics2d.GraphicsDriverMock
import com.aheidelbacher.algostorm.test.engine.input.InputDriverMock

import org.junit.Test

import com.aheidelbacher.algoventure.core.ui.UiHandler

class AlgoventureEngineTest : EngineTest() {
    private val audioDriver = AudioDriverMock()
    private val graphicsDriver = GraphicsDriverMock(320, 230)
    private val inputDriver = InputDriverMock()
    private val uiHandler = object : UiHandler {
        override fun onGameOver() {}

        override fun onGameWon() {}
    }
    override fun createEngine(): Engine = AlgoventureEngine(
            audioDriver = audioDriver,
            graphicsDriver = graphicsDriver,
            inputDriver = inputDriver,
            uiHandler = uiHandler,
            playerObjectType = "knight"
    )

    @Test
    fun engineSmokeTest() {
        val engine = createEngine()
        engine.start()
        repeat(100) {
            inputDriver.key(0)
            Thread.sleep(30)
        }
        engine.stop()
        engine.shutdown()
    }
}
