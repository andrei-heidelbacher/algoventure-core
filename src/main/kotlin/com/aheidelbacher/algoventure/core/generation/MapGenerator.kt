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

import com.aheidelbacher.algostorm.engine.Engine
import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algostorm.engine.state.Layer
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.TileSet

import java.io.InputStream

class MapGenerator(
        tiles: List<InputStream>,
        prototypes: kotlin.collections.Map<String, InputStream>
) {
    companion object {
        fun newMap(playerPrototype: String): Map {
            val tiles = Serializer.readValue<List<String>>(
                    Engine.getResource("/tile_sets.json")
            ).map { Engine.getResource(it) }
            val prototypes = Serializer.readValue<List<String>>(
                    Engine.getResource("/prototypes.json")
            ).associate { it to Engine.getResource(it) }
            return MapGenerator(tiles, prototypes).newMap(playerPrototype)
        }
    }

    private val tileSets = tiles.map { Serializer.readValue<TileSet>(it) }
    private val prototypes = prototypes.map {
        it.key to Serializer.readValue<PrototypeObject>(it.value)
    }.toMap()

    fun newMap(playerPrototype: String): Map {
        val playerObject = requireNotNull(
                prototypes[playerPrototype]?.toObject(1, 24, 24, 0F)
        ) { "Invalid prototype $playerPrototype!" }
        return Map(
                width = 32,
                height = 32,
                tileWidth = 24,
                tileHeight = 24,
                orientation = Map.Orientation.ORTHOGONAL,
                tileSets = tileSets,
                layers = listOf(
                        Layer.TileLayer(
                                name = "floor",
                                data = IntArray(32 * 32) { 540 + 453 + it % 3 }
                        ),
                        Layer.ObjectGroup(
                                name = "objects",
                                objects = hashSetOf(
                                        playerObject,
                                        Object(
                                                id = 2,
                                                x = 14 * 24,
                                                y = 14 * 24,
                                                width = 24,
                                                height = 24,
                                                gid = 540 + 451,
                                                properties = hashMapOf(
                                                        "isRigid" to true
                                                )
                                        )
                                )
                        )
                ),
                properties = hashMapOf(
                        "playerId" to 1,
                        "cameraX" to 16 * 24 + 12,
                        "cameraY" to 16 * 24 + 12
                ),
                nextObjectId = 3
        )
    }
}
