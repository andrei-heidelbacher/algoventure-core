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

package com.aheidelbacher.algoventure.core.input

import com.aheidelbacher.algostorm.engine.geometry2d.Point
import com.aheidelbacher.algostorm.engine.graphics2d.camera.Camera
import com.aheidelbacher.algostorm.engine.graphics2d.camera.CameraSystem.Scroll
import com.aheidelbacher.algostorm.engine.input.AbstractInputSystem
import com.aheidelbacher.algostorm.engine.input.InputReader
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Publisher

import com.aheidelbacher.algoventure.core.act.Action
import com.aheidelbacher.algoventure.core.ai.Util.findPath
import com.aheidelbacher.algoventure.core.geometry2d.Direction

class InputSystem(
        private val tileWidth: Int,
        private val tileHeight: Int,
        private val objectManager: ObjectManager,
        private val publisher: Publisher,
        private val objectId: Int,
        private val camera: Camera,
        inputReader: InputReader<Input>
) : AbstractInputSystem<Input>(inputReader) {
    private fun getObject(): Object? = objectManager[objectId]

    private fun putAction(action: Action) {
        getObject()?.set(Input.INPUT, action)
    }

    override fun handleInput(input: Input) {
        when (input) {
            is Input.Click -> {
                val x = (input.x + camera.x) / tileWidth
                val y = (input.y + camera.y) / tileHeight
                getObject()?.let { obj ->
                    val path = findPath(
                            objectManager = objectManager,
                            tileWidth = tileWidth,
                            tileHeight = tileHeight,
                            source = Point(obj.x / tileWidth, obj.y / tileHeight),
                            destination = Point(x / tileWidth, y / tileHeight)
                    )
                }
                val dx = input.x / tileWidth
                val dy = input.y / tileHeight
                Direction.getDirection(dx, dy)?.let { direction ->
                    getObject()?.let { obj ->
                        putAction(Action.Move(objectId, direction))
                    }
                }
            }
            is Input.Scroll -> {
                publisher.post(Scroll(input.dx, input.dy))
            }
            is Input.Wait -> {
                putAction(Action.Wait(objectId))
            }
        }
    }
}
