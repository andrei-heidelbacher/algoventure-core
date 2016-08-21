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

import com.aheidelbacher.algostorm.engine.Engine.Companion.getResourceStream
import com.aheidelbacher.algostorm.engine.geometry2d.Point
import com.aheidelbacher.algostorm.engine.graphics2d.camera.Camera.Companion.CAMERA_X
import com.aheidelbacher.algostorm.engine.graphics2d.camera.Camera.Companion.CAMERA_Y
import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algostorm.engine.state.Map
import com.aheidelbacher.algostorm.engine.state.TileSet

import com.aheidelbacher.algoventure.core.generation.MapGenerator
import com.aheidelbacher.algoventure.core.generation.PrototypeObject
import com.aheidelbacher.algoventure.core.generation.Random
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.ADJACENT_DIRECTIONS
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.DOOR
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.FLOOR
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.WALL
import com.aheidelbacher.algoventure.core.state.State
import com.aheidelbacher.algoventure.core.state.State.floor
import com.aheidelbacher.algoventure.core.state.State.objectGroup

class DungeonMapGenerator(
        width: Int,
        height: Int,
        tileWidth: Int,
        tileHeight: Int,
        tileSets: List<String>,
        prototypes: List<String>,
        private val floorGid: List<Long>,
        private val wallMaskGid: kotlin.collections.Map<Int, List<Long>>
) : MapGenerator<DungeonLevel>(
        width = width,
        height = height,
        tileWidth = tileWidth,
        tileHeight = tileHeight,
        orientation = Map.Orientation.ORTHOGONAL,
        tileSets = tileSets.map { path ->
            getResourceStream(path).use { stream ->
                Serializer.readValue<TileSet>(stream)
            }
        },
        prototypes = prototypes.associate { path ->
            val prototype = getResourceStream(path).use { stream ->
                Serializer.readValue<PrototypeObject>(stream)
            }
            prototype.type to prototype
        },
        levelGenerator = DungeonGenerator(
                levelWidth = width,
                levelHeight = height,
                minRoomSize = Math.min(width, height) / 8,
                maxRoomSize = Math.min(width, height) / 4,
                roomPlacementAttempts = width * height / 8,
                corridorStraightness = 0.8F
        )
) {
    companion object {
        fun newMap(playerPrototype: String): Map {
            val tiles = getResourceStream("/tile_sets.json").use {
                Serializer.readValue<List<String>>(it)
            }
            val prototypes = getResourceStream("/prototypes.json").use {
                Serializer.readValue<List<String>>(it)
            }
            return DungeonMapGenerator(
                    width = 32,
                    height = 32,
                    tileWidth = 24,
                    tileHeight = 24,
                    tileSets = tiles,
                    prototypes = prototypes,
                    floorGid = listOf(993, 993, 993, 993, 995),
                    wallMaskGid = mapOf(
                            0 to listOf(999L),
                            1 to listOf(1005L),
                            2 to listOf(1000L),
                            3 to listOf(1008L),
                            4 to listOf(1003L),
                            5 to listOf(1004L, 1004L, 1004L, 1004L, 1015L),
                            6 to listOf(1006L),
                            7 to listOf(1013L),
                            8 to listOf(1002L),
                            9 to listOf(1009L),
                            10 to listOf(1001L, 1001L, 1001L, 1001L, 1016L),
                            11 to listOf(1014L),
                            12 to listOf(1007L),
                            13 to listOf(1012L),
                            14 to listOf(1011L),
                            15 to listOf(1010L)
                    )
            ).generate(playerPrototype)
        }
    }

    private val wallPrototype = this.prototypes["wall"]
            ?: error("Missing wall prototype!")
    private val doorPrototype = this.prototypes["door"]
            ?: error("Missing door prototype!")

    private fun generatePoint(width: Int, height: Int): Point = Point(
            x = (Math.random() * width).toInt(),
            y = (Math.random() * height).toInt()
    )

    override fun Map.decorate() {
        val monsterPrototype = "monster"
        val actors = 8
        val actorLocations = mutableSetOf<Point>()
        for (i in 1..actors) {
            var point: Point
            do {
                point = generatePoint(width, height)
                val isFloor = floor.data[point.y * width + point.x] != 0L
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
                properties[CAMERA_X] = obj.x + obj.width / 2
                properties[CAMERA_Y] = obj.y + obj.height / 2
            }
            isPlayer = false
        }
    }

    private fun DungeonLevel.getAdjacencyMask(x: Int, y: Int, tile: Int): Int =
            ADJACENT_DIRECTIONS.foldIndexed(0) { i, mask, direction ->
                val nx = x + direction.dx
                val ny = y + direction.dy
                if (contains(nx, ny) && get(nx, ny) == tile) mask.or(1.shl(i))
                else mask
            }

    private fun Map.inflateTile(level: DungeonLevel, x: Int, y: Int) {
        val tile = level[x, y]
        floor.data[y * width + x] = 0
        when (tile) {
            FLOOR -> floor.data[y * width + x] =
                    floorGid[Random.nextInt(0, floorGid.size)]
            WALL -> {
                val mask = level.getAdjacencyMask(x, y, WALL)
                val gid = wallMaskGid[mask]?.let {
                    it[Random.nextInt(0, it.size)]
                } ?: error("Missing wall gid mask $mask!")
                val obj = wallPrototype.toObject(
                        id = getNextObjectId(),
                        x = x * tileWidth,
                        y = y * tileHeight
                )
                obj.gid = gid
                objectGroup.objects.add(obj)
            }
            DOOR -> {
                objectGroup.objects.add(doorPrototype.toObject(
                        id = getNextObjectId(),
                        x = x * tileWidth,
                        y = y * tileHeight
                ))
                floor.data[y * width + x] =
                        floorGid[Random.nextInt(0, floorGid.size)]
            }
            DungeonLevel.ENTRANCE -> { }
            DungeonLevel.EXIT -> { }
            else -> {}
        }
    }

    override fun Map.inflateLevel(level: DungeonLevel) {
        for (y in 0 until level.height) {
            for (x in 0 until level.width) {
                inflateTile(level, x, y)
            }
        }
    }
}
