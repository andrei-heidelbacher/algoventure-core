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

package com.aheidelbacher.algoventure.core.generation.dungeon

import com.aheidelbacher.algoventure.core.generation.Level
import com.aheidelbacher.algoventure.core.generation.LevelGenerator
import com.aheidelbacher.algoventure.core.geometry2d.Direction

import java.util.Collections

class DungeonGenerator(
        levelWidth: Int,
        levelHeight: Int,
        private val minRoomSize: Int,
        private val maxRoomSize: Int,
        private val roomPlacementAttempts: Int,
        private val corridorStraightness: Float
) : LevelGenerator(levelWidth = levelWidth, levelHeight = levelHeight) {
    private companion object {
        val DIRECTIONS: Array<Direction> = arrayOf(
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        )

        fun randomOddInt(lower: Int, upper: Int): Int =
                2 * randomInt(lower / 2, (upper + 1) / 2) + 1

        fun Level.canPlaceRoomAt(
                x: Int,
                y: Int,
                width: Int,
                height: Int
        ): Boolean {
            for (rx in x until x + width) {
                for (ry in y until y + height) {
                    if (get(rx, ry) != DungeonTile.EMPTY) {
                        return false
                    }
                }
            }
            return true
        }

        fun Level.placeRoomAt(x: Int, y: Int, width: Int, height: Int) {
            for (rx in x until x + width) {
                for (ry in y until y + height) {
                    set(rx, ry, DungeonTile.FLOOR)
                }
            }
        }

        fun Level.placeRooms(
                minSize: Int,
                maxSize: Int,
                placementAttempts: Int
        ) {
            repeat(placementAttempts) {
                val roomWidth = randomOddInt(minSize, maxSize)
                val roomHeight = randomOddInt(minSize, maxSize)
                val x = randomOddInt(0, width - roomWidth)
                val y = randomOddInt(0, height - roomHeight)
                if (canPlaceRoomAt(x, y, roomWidth, roomHeight)) {
                    placeRoomAt(x, y, roomWidth, roomHeight)
                }
            }
        }

        fun Level.floodFill(
                x: Int,
                y: Int,
                straightness: Float,
                previousDirection: Direction? = null
        ) {
            fun expand(d: Direction) {
                val nx = x + d.dx * 2
                val ny = y + d.dy * 2
                val canExpand = nx in 0 until width && ny in 0 until height &&
                        get(nx, ny) == DungeonTile.EMPTY
                if (canExpand) {
                    set(x + d.dx, y + d.dy, DungeonTile.FLOOR)
                    floodFill(nx, ny, straightness, d)
                }
            }

            set(x, y, DungeonTile.FLOOR)
            if (Math.random() < straightness && previousDirection != null) {
                expand(previousDirection)
            }
            val directions = DIRECTIONS.toMutableList()
            Collections.shuffle(directions)
            directions.forEach(::expand)
        }

        fun Level.placeCorridors(straightness: Float) {
            for (x in 1 until width step 2) {
                for (y in 1 until height step 2) {
                    if (get(x, y) == DungeonTile.EMPTY) {
                        floodFill(x, y, straightness)
                    }
                }
            }
        }

        fun Level.placeWalls() {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (get(x, y) == DungeonTile.EMPTY) {
                        val isAdjacentToFloor = Direction.values().any {
                            val nx = x + it.dx
                            val ny = y + it.dy
                            nx in 0 until width && ny in 0 until height &&
                                    get(nx, ny) == DungeonTile.FLOOR
                        }
                        if (isAdjacentToFloor) {
                            set(x, y, DungeonTile.WALL)
                        }
                    }
                }
            }
        }
    }

    init {
        require(0 < minRoomSize) {
            "Min room size $minRoomSize must be positive!"
        }
        require(minRoomSize < maxRoomSize) {
            "Min room size must be less than max room size!"
        }
        require(maxRoomSize <= levelWidth && maxRoomSize <= levelHeight) {
            "Max room size can't be greater than level sizes!"
        }
        require(0F <= corridorStraightness && corridorStraightness <= 1F) {
            "Corridor straightness $corridorStraightness must be within [0, 1]!"
        }
    }

    override fun generate(): Level {
        val level = Level(levelWidth, levelHeight, DungeonTile.EMPTY)
        level.placeRooms(minRoomSize, maxRoomSize, roomPlacementAttempts)
        level.placeCorridors(corridorStraightness)
        level.placeWalls()
        return level
    }
}
