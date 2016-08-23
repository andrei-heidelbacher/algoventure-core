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

import com.aheidelbacher.algostorm.engine.graphics2d.Canvas
import com.aheidelbacher.algostorm.engine.graphics2d.Matrix
import com.aheidelbacher.algostorm.engine.input.InputSocket
import com.aheidelbacher.algostorm.engine.sound.SoundEngine
import com.aheidelbacher.algostorm.engine.state.TileSet
import com.aheidelbacher.algoventure.core.input.Input
import com.aheidelbacher.algoventure.core.ui.UiHandler
import org.junit.Test

class AlgoventureEngineTest {
    var frames = 0
    val inputSocket = InputSocket<Input>()
    val canvas = object : Canvas {
        override val width: Int
            get() = 320

        override val height: Int
            get() = 320

        override fun loadBitmap(image: String) {
            println("Load bitmap $image")
        }

        override fun unloadBitmaps() {
            println("Unloaded bitmaps!")
        }

        override fun clear() {
            println("Clear canvas")
        }

        override fun lock() {
            println("Locked canvas")
        }

        override fun unlockAndPost() {
            println("Unlocked canvas")
            frames += 1
        }

        override fun drawBitmap(
                viewport: TileSet.Viewport,
                matrix: Matrix,
                opacity: Float
        ) {
            println("Draw ${matrix.getValues()}")
        }
    }
    val soundEngine = object : SoundEngine {
        override fun loadSound(sound: String) {
            println("Load sound $sound")
        }

        override fun loadMusic(musicSound: String) {
            println("Load music $musicSound")
        }

        override fun playMusic(sound: String, loop: Boolean) {}

        override fun stopMusic() {}

        override fun playSound(sound: String, loop: Boolean): Int = -1

        override fun stopStream(streamId: Int) {}

        override fun release() {}
    }
    val engine = AlgoventureEngine(
            "knight",
            Platform(canvas, soundEngine, inputSocket, object : UiHandler {
                override fun onGameOver() {

                }

                override fun onGameWon() {

                }
            })
    )

    @Test
    fun engineSmokeTest() {
        engine.start()
        Thread.sleep(1000)
        engine.stop()
        engine.shutdown()
        println("FPS: $frames. Target FPS: ${1000.0 / 25.0}")
    }
}
