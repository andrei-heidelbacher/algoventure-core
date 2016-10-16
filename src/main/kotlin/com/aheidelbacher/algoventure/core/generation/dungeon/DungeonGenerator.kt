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

import com.aheidelbacher.algostorm.systems.geometry2d.Point

import com.aheidelbacher.algoventure.core.generation.LevelGenerator
import com.aheidelbacher.algoventure.core.generation.Random
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.DOOR
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.EMPTY
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.FLOOR
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.WALL
import com.aheidelbacher.algoventure.core.geometry2d.Direction

import java.util.Collections
import java.util.LinkedList

class DungeonGenerator(
        levelWidth: Int,
        levelHeight: Int,
        private val minRoomSize: Int,
        private val maxRoomSize: Int,
        private val roomPlacementAttempts: Int,
        private val corridorStraightness: Float
) : LevelGenerator<DungeonLevel>(levelWidth, levelHeight) {
    private companion object {
        fun nextOddInt(lower: Int, upper: Int): Int =
                2 * Random.nextInt(lower / 2, (upper + 1) / 2) + 1

        fun DungeonLevel.canPlaceRoomAt(
                x: Int,
                y: Int,
                width: Int,
                height: Int
        ): Boolean {
            for (ry in y until y + height)
                for (rx in x until x + width)
                    if (get(rx, ry) != EMPTY)
                        return false
            return true
        }

        fun DungeonLevel.placeRoomAt(
                x: Int,
                y: Int,
                roomWidth: Int,
                roomHeight: Int,
                roomColor: Int
        ) {
            for (ry in y until y + roomHeight) {
                for (rx in x until x + roomWidth) {
                    set(rx, ry, FLOOR)
                    setColor(rx, ry, roomColor)
                }
            }
        }

        fun DungeonLevel.placeRooms(
                minSize: Int,
                maxSize: Int,
                placementAttempts: Int
        ): Int {
            var usedColors = 0
            repeat(placementAttempts) {
                val roomWidth = nextOddInt(minSize, maxSize)
                val roomHeight = nextOddInt(minSize, maxSize)
                val x = nextOddInt(1, width - roomWidth - 1)
                val y = nextOddInt(1, height - roomHeight - 1)
                if (canPlaceRoomAt(x, y, roomWidth, roomHeight)) {
                    usedColors += 1
                    placeRoomAt(
                            x = x,
                            y = y,
                            roomWidth = roomWidth,
                            roomHeight = roomHeight,
                            roomColor = usedColors
                    )
                }
            }
            return usedColors
        }

        fun DungeonLevel.floodFill(
                x: Int,
                y: Int,
                straightness: Float,
                previousDirection: Direction?,
                usedColors: Int
        ) {
            fun expand(d: Direction) {
                val nx = x + d.dx * 2
                val ny = y + d.dy * 2
                val canExpand = nx in 1 until width - 1 &&
                        ny in 1 until height - 1 &&
                        get(nx, ny) == EMPTY
                if (canExpand) {
                    set(x + d.dx, y + d.dy, FLOOR)
                    setColor(x + d.dx, y + d.dy, usedColors + 1)
                    floodFill(nx, ny, straightness, d, usedColors)
                }
            }

            set(x, y, DungeonLevel.FLOOR)
            setColor(x, y, usedColors + 1)
            if (Math.random() < straightness && previousDirection != null) {
                expand(previousDirection)
            }
            val directions = DungeonLevel.ADJACENT_DIRECTIONS.toMutableList()
            Collections.shuffle(directions)
            directions.forEach(::expand)
        }

        fun DungeonLevel.placeCorridors(
                straightness: Float,
                usedColorsForRooms: Int
        ) {
            var usedColors = usedColorsForRooms
            for (x in 1 until width - 1 step 2) {
                for (y in 1 until height - 1 step 2) {
                    if (get(x, y) == EMPTY) {
                        floodFill(x, y, straightness, null, usedColors)
                        usedColors += 1
                    }
                }
            }
        }

        fun DungeonLevel.placeDoors() {
            val father = hashMapOf<Int, Int>()
            fun getFather(color: Int): Int =
                    father[color]?.let(::getFather) ?: color

            val doors = mutableListOf<Point>()
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val isDoor = get(x, y) == EMPTY &&
                            countAdjacent(x, y, FLOOR, DOOR) == 2
                    if (isDoor) {
                        doors.add(Point(x, y))
                    }
                }
            }
            Collections.shuffle(doors)
            for ((x, y) in doors) {
                var firstColor = 0
                var secondColor = 0
                DungeonLevel.ADJACENT_DIRECTIONS.forEach {
                    val nx = x + it.dx
                    val ny = y + it.dy
                    if (contains(nx, ny) && get(nx, ny) == FLOOR) {
                        if (firstColor == 0) {
                            firstColor = getColor(nx, ny)
                        } else {
                            secondColor = getColor(nx, ny)
                        }
                    }
                }
                firstColor = getFather(firstColor)
                secondColor = getFather(secondColor)
                if (firstColor != secondColor) {
                    father[secondColor] = firstColor
                    set(x, y, DOOR)
                }
            }
        }

        private fun DungeonLevel.isDeadEnd(x: Int, y: Int): Boolean =
            contains(x, y) && (get(x, y) == FLOOR || get(x, y) == DOOR) &&
                    countAdjacent(x, y, FLOOR, DOOR) <= 1

        fun DungeonLevel.removeDeadEnds() {
            val queue = LinkedList<Point>()
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (isDeadEnd(x, y)) {
                        queue.add(Point(x, y))
                    }
                }
            }
            while (queue.isNotEmpty()) {
                val (x, y) = queue.remove()
                set(x, y, DungeonLevel.EMPTY)
                DungeonLevel.ADJACENT_DIRECTIONS.forEach {
                    val nx = x + it.dx
                    val ny = y + it.dy
                    if (isDeadEnd(nx, ny)) {
                        queue.add(Point(nx, ny))
                    }
                }
            }
        }

        /*fun DungeonLevel.removeDoorsToDeadEnds() {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (get(x, y) == DOOR && countAdjacent(x, y, FLOOR, DOOR) == 1) {
                        set(x, y, EMPTY)
                    }
                }
            }
        }*/

        fun DungeonLevel.placeWalls() {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val isWall = get(x, y) == EMPTY &&
                            countNeighboring(x, y, FLOOR, DOOR) > 0
                    if (isWall) {
                        set(x, y, WALL)
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

    override fun generate(): DungeonLevel {
        val level = DungeonLevel(levelWidth, levelHeight)
        val usedColors = level.placeRooms(
                minRoomSize,
                maxRoomSize,
                roomPlacementAttempts
        )
        level.placeCorridors(corridorStraightness, usedColors)
        level.placeDoors()
        level.removeDeadEnds()
        //level.removeDoorsToDeadEnds()
        level.placeWalls()
        return level
    }
}
