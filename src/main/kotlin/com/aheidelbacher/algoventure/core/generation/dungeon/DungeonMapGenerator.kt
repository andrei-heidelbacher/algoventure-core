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

import com.aheidelbacher.algostorm.engine.Engine
import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.TileSet
import com.aheidelbacher.algoventure.core.generation.MapGenerator
import com.aheidelbacher.algoventure.core.generation.PrototypeObject

import com.aheidelbacher.algoventure.core.geometry2d.Point
import com.aheidelbacher.algoventure.core.state.State
import com.aheidelbacher.algoventure.core.state.State.floor
import com.aheidelbacher.algoventure.core.state.State.objectGroup

import java.io.InputStream

class DungeonMapGenerator(
        width: Int,
        height: Int,
        tileWidth: Int,
        tileHeight: Int,
        tileSets: List<InputStream>,
        prototypes: kotlin.collections.Map<String, InputStream>
) : MapGenerator(
        width = width,
        height = height,
        tileWidth = tileWidth,
        tileHeight = tileHeight,
        orientation = Map.Orientation.ORTHOGONAL,
        tileSets = tileSets.map { Serializer.readValue<TileSet>(it) },
        prototypes = prototypes.map {
            it.key to Serializer.readValue<PrototypeObject>(it.value)
        }.toMap(),
        levelGenerator = DungeonGenerator(
                levelWidth = width,
                levelHeight = height,
                minRoomSize = Math.min(width, height) / 8,
                maxRoomSize = Math.min(width, height) / 4,
                roomPlacementAttempts = Math.sqrt(1.0 * width * height).toInt(),
                corridorStraightness = 0.9F
        )
) {
    companion object {
        fun newMap(playerPrototype: String): Map {
            val tiles = Serializer.readValue<List<String>>(
                    Engine.getResource("/tile_sets.json")
            ).map { Engine.getResource(it) }
            val prototypes = Serializer.readValue<List<String>>(
                    Engine.getResource("/prototypes.json")
            ).associate { it to Engine.getResource(it) }
            return DungeonMapGenerator(
                    width = 32 * 16,
                    height = 32,
                    tileWidth = 24,
                    tileHeight = 24,
                    tileSets = tiles,
                    prototypes = prototypes
            ).generate(playerPrototype)
        }
    }

    fun generatePoint(width: Int, height: Int): Point = Point(
            x = (Math.random() * width).toInt(),
            y = (Math.random() * height).toInt()
    )

    override fun Map.decorate() {
        val monsterPrototype = "/prototypes/monster.json"
        val actors = 8 * 16
        val actorLocations = mutableSetOf<Point>()
        for (i in 1..actors) {
            var point: Point
            do {
                point = generatePoint(width, height)
                val isFloor = floor.data[point.y * width + point.x] != 0
            } while (!isFloor || point in actorLocations)
            actorLocations.add(point)
        }
        var isPlayer = true
        for ((px, py) in actorLocations) {
            val x = px * tileWidth
            val y = py * tileHeight
            val obj = if (isPlayer) playerPrototype
                    .toObject(getNextObjectId(), x, y)
            else requireNotNull(prototypes[monsterPrototype])
                    .toObject(getNextObjectId(), x, y)
            objectGroup.objects.add(obj)
            if (isPlayer) {
                properties[State.PLAYER_OBJECT_ID_PROPERTY] = obj.id
                properties[State.CAMERA_X_PROPERTY] = obj.x + obj.width / 2
                properties[State.CAMERA_Y_PROPERTY] = obj.y + obj.height / 2
            }
            isPlayer = false
        }
    }

    override fun Map.inflateTile(x: Int, y: Int, tile: Int) {
        val wallPrototype = prototypes["/prototypes/wall.json"]
                ?: error("Missing wall prototype!")
        val doorPrototype = prototypes["/prototypes/door.json"]
                ?: error("Missing door prototype!")
        floor.data[y * width + x] = 0
        when (tile) {
            DungeonTile.FLOOR -> floor.data[y * width + x] = 540 + 453
            DungeonTile.WALL -> objectGroup.objects.add(wallPrototype.toObject(
                    id = getNextObjectId(),
                    x = x * tileWidth,
                    y = y * tileHeight
            ))
            DungeonTile.DOOR -> objectGroup.objects.add(doorPrototype.toObject(
                    id = getNextObjectId(),
                    x = x * tileWidth,
                    y = y * tileHeight
            ))
            DungeonTile.ENTRANCE -> { }
            DungeonTile.EXIT -> { }
            else -> {}
        }
    }
}
