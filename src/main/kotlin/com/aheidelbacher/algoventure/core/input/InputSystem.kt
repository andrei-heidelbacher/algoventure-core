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

import com.aheidelbacher.algostorm.engine.input.InputSource
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.systems.geometry2d.Point
import com.aheidelbacher.algostorm.systems.graphics2d.camera.Camera
import com.aheidelbacher.algostorm.systems.graphics2d.camera.CameraSystem.Scroll
import com.aheidelbacher.algostorm.systems.input.AbstractInputSystem

import com.aheidelbacher.algoventure.core.act.Action
import com.aheidelbacher.algoventure.core.ai.findPath
import com.aheidelbacher.algoventure.core.geometry2d.Direction

class InputSystem(
        inputSource: InputSource,
        private val tileWidth: Int,
        private val tileHeight: Int,
        private val objectGroup: ObjectGroup,
        private val publisher: Publisher,
        private val objectId: Int,
        private val camera: Camera
) : AbstractInputSystem(inputSource) {
    companion object {
        private val lastAction = hashMapOf<Int, Action>()

        fun fetchLastAction(objectId: Int): Action? {
            val action = lastAction[objectId]
            lastAction.remove(objectId)
            return action
        }
    }

    private fun getObject(): Object? = objectGroup[objectId]

    private fun putAction(action: Action) {
        lastAction[objectId] = action
    }

    override fun onTouch(x: Int, y: Int) {
        val tx = (x + camera.x) / tileWidth
        val ty = (y + camera.y) / tileHeight
        /*getObject()?.let { obj ->
            val path = findPath(
                    objectGroup = objectGroup,
                    tileWidth = tileWidth,
                    tileHeight = tileHeight,
                    source = Point(obj.x / tileWidth, obj.y / tileHeight),
                    destination = Point(x / tileWidth, y / tileHeight)
            )
        }*/
        val dx = x / tileWidth
        val dy = y / tileHeight
        Direction.getDirection(dx, dy)?.let { direction ->
            getObject()?.let { obj ->
                putAction(Action.Move(objectId, direction))
            }
        }
    }

    override fun onScroll(dx: Int, dy: Int) {
        publisher.post(Scroll(dx, dy))
    }

    override fun onKey(keyCode: Int) {
        putAction(Action.Wait(objectId))
    }
}
