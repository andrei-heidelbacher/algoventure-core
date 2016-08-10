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

package com.aheidelbacher.algoventure.core.facing

import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.engine.state.TileSet.Tile.Companion.flipHorizontally
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.attack.Attacked
import com.aheidelbacher.algoventure.core.facing.Facing.Companion.PROPERTY
import com.aheidelbacher.algoventure.core.facing.Facing.Companion.facing
import com.aheidelbacher.algoventure.core.geometry2d.Direction
import com.aheidelbacher.algoventure.core.move.Moved

class FacingSystem(
        private val objectManager: ObjectManager
) : Subscriber {
    private fun Object.updateFacing(direction: Direction) {
        facing?.let {
            val newFacing = when (direction) {
                Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST ->
                    Facing.RIGHT
                Direction.NORTH_WEST, Direction.WEST, Direction.SOUTH_WEST ->
                    Facing.LEFT
                else -> it
            }
            if (newFacing != it) {
                gid = gid.flipHorizontally()
            }
            properties[PROPERTY] = newFacing
        }
    }

    @Subscribe fun handleMoved(event: Moved) {
        objectManager[event.objectId]?.updateFacing(event.direction)
    }

    @Subscribe fun handleAttacked(event: Attacked) {
        objectManager[event.objectId]?.updateFacing(event.direction)
    }
}
