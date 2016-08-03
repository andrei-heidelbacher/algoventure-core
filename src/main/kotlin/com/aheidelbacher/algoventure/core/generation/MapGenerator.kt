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

import com.aheidelbacher.algostorm.state.Layer
import com.aheidelbacher.algostorm.state.Map
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.state.TileSet

object MapGenerator {
    fun newMap() = Map(
            width = 32,
            height = 32,
            tileWidth = 24,
            tileHeight = 24,
            orientation = Map.Orientation.ORTHOGONAL,
            tileSets = listOf(
                    TileSet(
                            name = "oryx_world",
                            tileWidth = 24,
                            tileHeight = 24,
                            tileCount = 2296,
                            image = "oryx_16bit_fantasy_world_trans.png",
                            imageWidth = 1344,
                            imageHeight = 984,
                            margin = 0,
                            spacing = 0
                    ),
                    TileSet(
                            name = "oryx_creatures",
                            tileWidth = 24,
                            tileHeight = 24,
                            tileCount = 540,
                            image = "oryx_16bit_fantasy_creatures_trans.png",
                            imageWidth = 480,
                            imageHeight = 648,
                            margin = 0,
                            spacing = 0,
                            tiles = mapOf(21 to TileSet.Tile(
                                    animation = listOf(
                                            TileSet.Tile.Frame(21, 500),
                                            TileSet.Tile.Frame(41, 500)
                                    )
                            ))
                    )
            ),
            layers = listOf(
                    Layer.TileLayer(
                            name = "floor",
                            data = IntArray(32 * 32) { 453 + it % 3 }
                    ),
                    Layer.ObjectGroup(
                            name = "objects",
                            objects = hashSetOf(Object(
                                    id = 1,
                                    x = 16 * 24,
                                    y = 16 * 24,
                                    width = 24,
                                    height = 24,
                                    gid = 2318
                            ))
                    )
            ),
            properties = hashMapOf(
                    "playerId" to 1,
                    "cameraX" to 16 * 24,
                    "cameraY" to 16 * 24
            ),
            nextObjectId = 2
    )
}
