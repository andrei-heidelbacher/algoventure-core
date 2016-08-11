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

package com.aheidelbacher.algoventure.core.generation

import com.aheidelbacher.algoventure.core.geometry2d.Point

class Level private constructor(
        val width: Int,
        val height: Int,
        val data: Array<Tile>
) {
    constructor(width: Int, height: Int, tile: Tile = Tile.EMPTY) : this(
            width = width,
            height = height,
            data = Array(width * height) { tile }
    )

    private val size: Int
        get() = width * height

    init {
        require(width > 0 && height > 0) {
            "Level sizes ($width, $height) must be positive!"
        }
        require(data.size == size)
    }

    fun getIndex(x: Int, y: Int): Int {
        require(x in 0 until width && y in 0 until height) {
            "Index ($x, $y) out of bounds ($width, $height)!"
        }
        return y * width + x
    }

    fun getIndex(p: Point): Int = getIndex(p.x, p.y)

    fun getX(index: Int): Int {
        require(index in 0 until size) {
            "Index $index out of bounds $size!"
        }
        return index % width
    }

    fun getY(index: Int): Int {
        require(index in 0 until size) {
            "Index $index out of bounds $size!"
        }
        return index / width
    }

    fun getEntrance(): Point {
        require(data.count { it == Tile.ENTRANCE } == 1) {
            "Invalid entrance count!"
        }
        return data.indexOf(Tile.ENTRANCE).let {
            Point(it % width, it / width)
        }
    }

    fun getExit(): Point {
        require(data.count { it == Tile.EXIT } == 1) {
            "Invalid exit count!"
        }
        return data.indexOf(Tile.EXIT).let {
            Point(it % width, it / width)
        }
    }

    operator fun get(x: Int, y: Int): Tile = data[getIndex(x, y)]

    operator fun get(p: Point): Tile = data[getIndex(p)]

    operator fun set(x: Int, y: Int, value: Tile) {
        data[getIndex(x, y)] = value
    }

    operator fun set(p: Point, value: Tile) {
        data[getIndex(p)] = value
    }
}
