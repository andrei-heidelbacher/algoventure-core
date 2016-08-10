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
import com.aheidelbacher.algoventure.core.geometry2d.Point

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

    fun generatePoint(width: Int, height: Int): Point =
            Point((Math.random() * width).toInt(), (Math.random() * height).toInt())

    private val tileSets = tiles.map { Serializer.readValue<TileSet>(it) }
    private val prototypes = prototypes.map {
        it.key to Serializer.readValue<PrototypeObject>(it.value)
    }.toMap()

    fun newMap(playerPrototype: String): Map {
        /*val playerObject = requireNotNull(
                prototypes[playerPrototype]?.toObject(1, 24, 24, 0F)
        ) { "Invalid prototype $playerPrototype!" }*/
        val monsterPrototype = "/prototypes/monster.json"
        val width = 32
        val height = 32
        val maxSize = 16
        val tileWidth = 24
        val tileHeight = 24
        val wallGid = 540 + 451
        val dungeon = DungeonGenerator.generate(width, height, maxSize)
        val actors = 8
        val actorLocations = mutableSetOf<Point>()
        for (i in 1..actors) {
            var spawnLocation = generatePoint(width, height)
            while (dungeon[spawnLocation] != DungeonGenerator.Tile.FLOOR ||
                    spawnLocation in actorLocations) {
                spawnLocation = generatePoint(width, height)
            }
            actorLocations.add(spawnLocation)
        }
        val floor = IntArray(width * height) { it % 3 }
        val objects = hashSetOf<Object>()
        var nextObjectId = 1
        for ((point, tile) in dungeon) {
            if (tile == DungeonGenerator.Tile.FLOOR) {
                floor[point.x * width + point.y] += 540 + 453
            } else if (tile == DungeonGenerator.Tile.WALL) {
                objects.add(Object(
                        id = nextObjectId,
                        x = point.x * tileWidth,
                        y = point.y * tileHeight,
                        width = tileWidth,
                        height = tileHeight,
                        gid = wallGid,
                        properties = hashMapOf("isRigid" to true)
                ))
                nextObjectId += 1
            } else if (tile == DungeonGenerator.Tile.EMPTY) {
                floor[point.x * width + point.y] = 0
            }
        }
        var isPlayer = true
        var playerId = 0
        var cameraX = 0
        var cameraY = 0
        for (point in actorLocations) {
            val obj = if (isPlayer) requireNotNull(prototypes[playerPrototype])
                    .toObject(nextObjectId, tileWidth, tileHeight, 0F)
            else requireNotNull(prototypes[monsterPrototype])
                    .toObject(nextObjectId, tileWidth, tileHeight, 0F)
            objects.add(obj)
            nextObjectId += 1
            if (isPlayer) {
                playerId = obj.id
                cameraX = obj.x + obj.width / 2
                cameraY = obj.y + obj.height / 2
            }
            isPlayer = false
        }
        return Map(
                width = width,
                height = height,
                tileWidth = tileWidth,
                tileHeight = tileHeight,
                orientation = Map.Orientation.ORTHOGONAL,
                tileSets = tileSets,
                layers = listOf(
                        Layer.TileLayer(
                                name = "floor",
                                data = floor
                        ),
                        Layer.ObjectGroup(
                                name = "objects",
                                objects = objects
                        )
                ),
                properties = hashMapOf(
                        "playerId" to playerId,
                        "cameraX" to cameraX,
                        "cameraY" to cameraY
                ),
                nextObjectId = nextObjectId
        )
    }
}
