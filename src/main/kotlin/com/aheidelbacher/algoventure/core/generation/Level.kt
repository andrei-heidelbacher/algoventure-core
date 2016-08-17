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

abstract class Level private constructor(
        val width: Int,
        val height: Int,
        val data: IntArray
) {
    constructor(width: Int, height: Int, defaultTile: Int) : this(
            width = width,
            height = height,
            data = IntArray(width * height) { defaultTile }
    )

    protected val size: Int
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

    fun contains(x: Int, y: Int): Boolean =
            x in 0 until width && y in 0 until height

    operator fun get(x: Int, y: Int): Int = data[getIndex(x, y)]

    operator fun set(x: Int, y: Int, value: Int) {
        data[getIndex(x, y)] = value
    }
}
