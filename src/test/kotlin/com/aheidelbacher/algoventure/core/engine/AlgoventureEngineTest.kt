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

import com.aheidelbacher.algostorm.engine.input.InputSocket
import com.aheidelbacher.algostorm.engine.sound.SoundEngine
import com.aheidelbacher.algostorm.test.engine.EngineTest
import com.aheidelbacher.algostorm.test.engine.graphics2d.CanvasMock

import org.junit.Test

import com.aheidelbacher.algoventure.core.input.Input
import com.aheidelbacher.algoventure.core.ui.UiHandler

class AlgoventureEngineTest private constructor(
        private val inputSocket: InputSocket<Input>,
        private val canvas: CanvasMock,
        soundEngine: SoundEngine,
        uiHandler: UiHandler
) : EngineTest(AlgoventureEngine(
        "knight",
        Platform(canvas, soundEngine, inputSocket, uiHandler)
)) {
    constructor() : this(
            inputSocket = InputSocket<Input>(),
            canvas = CanvasMock(),
            soundEngine = object : SoundEngine {
                override fun loadSound(soundSource: String) {
                    println("Load sound $soundSource")
                }

                override fun loadMusic(musicSource: String) {
                    println("Load music $musicSource")
                }

                override fun playMusic(musicSource: String, loop: Boolean) {}

                override fun stopMusic() {}

                override fun playSound(soundSource: String): Int = -1

                override fun stopStream(streamId: Int) {}

                override fun release() {}
            },
            uiHandler = object : UiHandler {
                override fun onGameOver() {}

                override fun onGameWon() {}
            }
    )

    private fun isEmptyCanvas(): Boolean = try {
        canvas.verifyEmptyDrawQueue()
        true
    } catch (e: IllegalStateException) {
        false
    }

    private fun isClear(): Boolean = try {
        canvas.verifyClear()
        true
    } catch (e: IllegalStateException) {
        false
    }

    override fun getElapsedFrames(): Int {
        var frames = 0
        while (!isEmptyCanvas()) {
            frames += if (isClear()) 1 else 0
        }
        return frames
    }

    @Test
    fun engineSmokeTest() {
        engine.start()
        repeat(10000) {
            inputSocket.writeInput(Input.Wait)
        }
        engine.stop()
        engine.shutdown()
        println(getElapsedFrames())
    }
}
