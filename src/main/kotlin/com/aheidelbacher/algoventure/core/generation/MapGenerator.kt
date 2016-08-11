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

import com.aheidelbacher.algostorm.engine.state.Layer
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.TileSet
import com.aheidelbacher.algoventure.core.state.State

abstract class MapGenerator(
        val width: Int,
        val height: Int,
        val tileWidth: Int,
        val tileHeight: Int,
        val orientation: Map.Orientation,
        val tileSets: List<TileSet>,
        val prototypes: kotlin.collections.Map<String, PrototypeObject>,
        val levelGenerator: LevelGenerator
) : TileInflater {
    protected lateinit var playerPrototype: PrototypeObject

    init {
        require(width > 0 && height > 0) {
            "Map sizes ($width, $height) must be positive!"
        }
        require(tileWidth > 0 && tileHeight > 0) {
            "Map tile sizes ($tileWidth, $tileHeight) must be positive!"
        }
        require(levelGenerator.levelWidth <= width) {
            "Level generator width is greater than map width!"
        }
        require(levelGenerator.levelHeight <= height) {
            "Level generator height is greater than map height!"
        }
    }

    abstract fun Map.decorate(): Unit

    fun generate(playerPrototypeName: String): Map {
        playerPrototype = requireNotNull(prototypes[playerPrototypeName]) {
            "Player prototype $playerPrototypeName not found!"
        }
        val map = Map(
                width = width,
                height = height,
                tileWidth = tileWidth,
                tileHeight = tileHeight,
                orientation = orientation,
                tileSets = tileSets,
                layers = listOf(
                        Layer.TileLayer(
                                name = State.FLOOR_TILE_LAYER_NAME,
                                data = IntArray(width * height) { 0 }
                        ),
                        Layer.ObjectGroup(
                                name = State.OBJECT_GROUP_NAME,
                                objects = hashSetOf()
                        )
                ),
                nextObjectId = 1
        )
        val level = levelGenerator.generate()
        for (x in 0 until width) {
            for (y in 0 until height) {
                map.inflateTile(x, y, level[x, y])
            }
        }
        map.decorate()
        return map
    }
}
