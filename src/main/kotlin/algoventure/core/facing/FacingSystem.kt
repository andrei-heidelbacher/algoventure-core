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

package algoventure.core.facing

import algostorm.event.Subscribe
import algostorm.event.Subscriber
import algostorm.state.Object
import algostorm.state.ObjectManager
import algoventure.core.attack.Attacked

import algoventure.core.facing.Facing.Companion.facing
import algoventure.core.geometry2d.Direction
import algoventure.core.move.Moved

class FacingSystem(
        private val objectManager: ObjectManager
) : Subscriber {
    private fun Object.updateFacing(direction: Direction) {
        facing?.let {
            when (direction) {
                Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST -> {
                    properties[Facing.PROPERTY] = Facing.RIGHT
                }
                Direction.NORTH_WEST, Direction.WEST, Direction.SOUTH_WEST -> {
                    properties[Facing.PROPERTY] = Facing.LEFT
                }
                else -> {}
            }
        }
    }

    @Subscribe fun handleMoved(event: Moved) {
        objectManager[event.objectId]?.updateFacing(event.direction)
    }

    @Subscribe fun handleAttacked(event: Attacked) {
        objectManager[event.objectId]?.updateFacing(event.direction)
    }
}
