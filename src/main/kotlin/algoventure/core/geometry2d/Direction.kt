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

package algoventure.core.geometry2d

/**
 * The eight cardinal directions in which movement is possible.
 */
enum class Direction {
    NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

    companion object {
        fun getDirection(dx: Int, dy: Int): Direction? = when {
            dx == 0 && dy < 0 -> Direction.NORTH
            dx > 0 && dy < 0 -> Direction.NORTH_EAST
            dx > 0 && dy == 0 -> Direction.EAST
            dx > 0 && dy > 0 -> Direction.SOUTH_EAST
            dx == 0 && dy > 0 -> Direction.SOUTH
            dx < 0 && dy > 0 -> Direction.SOUTH_WEST
            dx < 0 && dy == 0 -> Direction.WEST
            dx < 0 && dy < 0 -> Direction.NORTH_WEST
            else -> null
        }
    }

    val dx: Int
        get() = when (this) {
            Direction.NORTH, Direction.SOUTH -> 0
            Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST -> 1
            Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST -> -1
        }

    val dy: Int
        get() = when (this) {
            Direction.EAST, Direction.WEST -> 0
            Direction.NORTH, Direction.NORTH_EAST, Direction.NORTH_WEST -> -1
            Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST -> 1
        }
}
