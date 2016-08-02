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

import com.aheidelbacher.algostorm.input.AbstractInputSystem
import com.aheidelbacher.algostorm.input.InputSocket
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.state.ObjectManager

import com.aheidelbacher.algoventure.core.act.Action
import com.aheidelbacher.algoventure.core.geometry2d.Direction

class InputSystem(
        private val objectManager: ObjectManager,
        private val objectId: Int,
        inputSocket: InputSocket<Input>
) : AbstractInputSystem<Input>(inputSocket) {
    companion object {
        const val PROPERTY: String = "lastInput"
    }

    private fun getObject(): Object? = objectManager[objectId]

    private fun putAction(action: Action) {
        getObject()?.properties?.put(PROPERTY, action)
    }

    override fun handleInput(input: Input?) {
        when (input) {
            is Input.Click -> {
                Direction.getDirection(input.x, input.y)?.let { direction ->
                    getObject()?.let { obj ->
                        putAction(Action.Move(objectId, direction))
                    }
                }
            }
            is Input.Scroll -> {}
            is Input.Wait -> {
                putAction(Action.Wait(objectId))
            }
            else -> {}
        }
    }
}
