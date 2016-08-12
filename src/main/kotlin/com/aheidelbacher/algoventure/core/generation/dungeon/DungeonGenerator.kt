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
import com.aheidelbacher.algoventure.core.geometry2d.Point

import java.util.Collections
import java.util.LinkedList

class DungeonGenerator(
        levelWidth: Int,
        levelHeight: Int,
        private val minRoomSize: Int,
        private val maxRoomSize: Int,
        private val roomPlacementAttempts: Int,
        private val corridorStraightness: Float
) : LevelGenerator(levelWidth = levelWidth, levelHeight = levelHeight) {
    private companion object {
        val MAX_DEPTH = 64
        val DIRECTIONS: Array<Direction> = arrayOf(
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        )

        fun randomOddInt(lower: Int, upper: Int): Int =
                2 * randomInt(lower / 2, (upper + 1) / 2) + 1

        fun Level.countAdj(x: Int, y: Int) = Direction.values().count {
            val nx = x + it.dx
            val ny = y + it.dy
            nx in 0 until width && ny in 0 until height &&
                    get(nx, ny) == DungeonTile.FLOOR
        }

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

        fun Level.placeRoomAt(
                x: Int,
                y: Int,
                width: Int,
                height: Int,
                colors: IntArray,
                roomColor: Int
        ) {
            for (rx in x until x + width) {
                for (ry in y until y + height) {
                    set(rx, ry, DungeonTile.FLOOR)
                    colors[ry * width + rx] = roomColor
                }
            }
        }

        fun Level.placeRooms(
                minSize: Int,
                maxSize: Int,
                placementAttempts: Int,
                colors: IntArray
        ): Int {
            var usedColors = 0
            repeat(placementAttempts) {
                val roomWidth = randomOddInt(minSize, maxSize)
                val roomHeight = randomOddInt(minSize, maxSize)
                val x = randomOddInt(0, width - roomWidth)
                val y = randomOddInt(0, height - roomHeight)
                if (canPlaceRoomAt(x, y, roomWidth, roomHeight)) {
                    usedColors += 1
                    placeRoomAt(
                            x = x,
                            y = y,
                            width = roomWidth,
                            height = roomHeight,
                            colors = colors,
                            roomColor = usedColors
                    )
                }
            }
            return usedColors
        }

        fun Level.floodFill(
                x: Int,
                y: Int,
                straightness: Float,
                previousDirection: Direction?,
                depth: Int,
                colors: IntArray,
                usedColors: Int
        ) {
            fun expand(d: Direction) {
                if (depth == MAX_DEPTH) {
                    return
                }
                val nx = x + d.dx * 2
                val ny = y + d.dy * 2
                val canExpand = nx in 0 until width && ny in 0 until height &&
                        get(nx, ny) == DungeonTile.EMPTY
                if (canExpand) {
                    set(x + d.dx, y + d.dy, DungeonTile.FLOOR)
                    colors[(y + d.dy) * width + x + d.dx] = usedColors + 1
                    floodFill(
                            nx,
                            ny,
                            straightness,
                            d,
                            depth + 1,
                            colors,
                            usedColors
                    )
                }
            }

            set(x, y, DungeonTile.FLOOR)
            colors[y * width + x] = usedColors + 1
            if (Math.random() < straightness && previousDirection != null) {
                expand(previousDirection)
            }
            val directions = DIRECTIONS.toMutableList()
            Collections.shuffle(directions)
            directions.forEach(::expand)
        }

        fun Level.placeCorridors(
                straightness: Float,
                colors: IntArray,
                usedColorsForRooms: Int
        ) {
            var usedColors = usedColorsForRooms
            for (x in 1 until width step 2) {
                for (y in 1 until height step 2) {
                    if (get(x, y) == DungeonTile.EMPTY) {
                        floodFill(
                                x,
                                y,
                                straightness,
                                null,
                                0,
                                colors,
                                usedColors
                        )
                        usedColors += 1
                    }
                }
            }
        }

        fun Level.placeDoors(colors: IntArray) {
            val father = hashMapOf<Int, Int>()
            fun getFather(color: Int): Int =
                    father[color]?.let(::getFather) ?: color

            val doors = mutableListOf<Point>()
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (get(x, y) == DungeonTile.EMPTY && countAdj(x, y) == 2) {
                        doors.add(Point(x, y))
                    }
                }
            }
            Collections.shuffle(doors)
            for ((x, y) in doors) {
                var firstColor = 0
                var secondColor = 0
                DIRECTIONS.forEach {
                    val nx = x + it.dx
                    val ny = y + it.dy
                    val isAdj = nx in 0 until width && ny in 0 until height &&
                            get(nx, ny) == DungeonTile.FLOOR
                    if (isAdj) {
                        if (firstColor == 0) {
                            firstColor = colors[ny * width + nx]
                        } else {
                            secondColor = colors[ny * width + nx]
                        }
                    }
                }
                if (getFather(firstColor) != getFather(secondColor)) {
                    father[secondColor] = firstColor
                    set(x, y, DungeonTile.DOOR)
                }
            }
        }

        fun Level.removeDeadEnds() {
            val queue = LinkedList<Point>()
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (get(x, y) == DungeonTile.FLOOR && countAdj(x, y) <= 1) {
                        queue.add(Point(x, y))
                    }
                }
            }
            while (queue.isNotEmpty()) {
                val (x, y) = queue.remove()
                set(x, y, DungeonTile.EMPTY)
                DIRECTIONS.forEach {
                    val nx = x + it.dx
                    val ny = y + it.dy
                    val isDeadEnd = nx in 0 until width &&
                            ny in 0 until height &&
                            get(nx, ny) == DungeonTile.FLOOR &&
                            countAdj(nx, ny) == 1
                    if (isDeadEnd) {
                        queue.add(Point(nx, ny))
                    }
                }
            }
        }

        fun Level.placeWalls() {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (get(x, y) == DungeonTile.EMPTY && countAdj(x, y) > 0) {
                        set(x, y, DungeonTile.WALL)
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
        val colors = IntArray(levelWidth * levelHeight)
        val usedColors = level.placeRooms(
                minRoomSize,
                maxRoomSize,
                roomPlacementAttempts,
                colors
        )
        level.placeCorridors(corridorStraightness, colors, usedColors)
        level.placeDoors(colors)
        level.removeDeadEnds()
        level.placeWalls()
        return level
    }
}
