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
import com.aheidelbacher.algoventure.core.act.Action.Move
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
        private val inputStateMachines = hashMapOf<Int, InputStateMachine>()

        fun getAction(objectId: Int): Action? =
                inputStateMachines[objectId]?.getAction()
    }

    private class InputStateMachine(private val id: Int) {
        private var actions: List<Action> = emptyList()

        fun getAction(): Action? {
            val action = actions.firstOrNull()
            actions = actions.drop(1)
            return action
        }

        fun setAction(action: Action?) {
            if (actions.isNotEmpty() || action == null) {
                actions = emptyList()
            } else {
                actions = listOf(action)
            }
        }

        fun setPath(path: List<Direction>?) {
            if (actions.isNotEmpty() || path == null) {
                actions = emptyList()
            } else {
                actions = path.map { Move(id, it) }
            }
        }
    }

    object KeyCodes {
        const val WAIT: Int = 0
        const val MUTE_MUSIC: Int = 1
        const val MUTE_SOUND_EFFECTS: Int = 2
        const val ZOOM_IN: Int = 3
        const val ZOOM_OUT: Int = 4
        const val TOGGLE_INVENTORY: Int = 5
    }

    init {
        inputStateMachines[objectId] = InputStateMachine(objectId)
    }

    override fun onTouch(x: Int, y: Int) {
        val tx = (x + camera.x) / tileWidth
        val ty = (y + camera.y) / tileHeight
        objectGroup[objectId]?.let { obj ->
            println("Touched at ($tx, $ty)!")
            println("Source at (${obj.x / tileWidth}, ${obj.y / tileHeight}.")
            val path = findPath(
                    objectGroup = objectGroup,
                    tileWidth = tileWidth,
                    tileHeight = tileHeight,
                    source = Point(obj.x / tileWidth, obj.y / tileHeight),
                    destination = Point(tx, ty)
            )
            println("Received path: $path")
            inputStateMachines[objectId]?.setPath(path)
        }
        /*val dx = x / tileWidth
        val dy = y / tileHeight
        Direction.getDirection(dx, dy)?.let { direction ->
            inputStateMachines[objectId]
                    ?.setAction(Action.Move(objectId, direction))
        }*/
    }

    override fun onScroll(dx: Int, dy: Int) {
        publisher.publish(Scroll(dx, dy))
    }

    override fun onKey(keyCode: Int) {
        inputStateMachines[objectId]?.setAction(Action.Wait(objectId))
    }
}
