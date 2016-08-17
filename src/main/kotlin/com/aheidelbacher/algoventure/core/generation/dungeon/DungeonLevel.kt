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
import com.aheidelbacher.algoventure.core.geometry2d.Direction

class DungeonLevel(width: Int, height: Int) : Level(
        width = width,
        height = height,
        defaultTile = EMPTY
) {
    companion object {
        const val EMPTY: Int = 0
        const val FLOOR: Int = 1
        const val WALL: Int = 2
        const val DOOR: Int = 3
        const val ENTRANCE: Int = 4
        const val EXIT: Int = 5

        val ADJACENT_DIRECTIONS: List<Direction> = listOf(
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        )

        val NEIGHBORING_DIRECTIONS: List<Direction> =
                Direction.values().toList()
    }

    private val colors = IntArray(size) { 0 }

    private fun countTiles(
            directions: List<Direction>,
            x: Int,
            y: Int,
            vararg tiles: Int
    ): Int = directions.count {
        val nx = x + it.dx
        val ny = y + it.dy
        contains(nx, ny) && tiles.any { it == get(nx, ny) }
    }

    fun countAdjacent(x: Int, y: Int, vararg tiles: Int): Int =
            countTiles(ADJACENT_DIRECTIONS, x, y, *tiles)

    fun countNeighboring(x: Int, y: Int, vararg tiles: Int): Int =
            countTiles(NEIGHBORING_DIRECTIONS, x, y, *tiles)

    fun getColor(x: Int, y: Int): Int = colors[getIndex(x, y)]

    fun setColor(x: Int, y: Int, c: Int) {
        colors[getIndex(x, y)] = c
    }
}
