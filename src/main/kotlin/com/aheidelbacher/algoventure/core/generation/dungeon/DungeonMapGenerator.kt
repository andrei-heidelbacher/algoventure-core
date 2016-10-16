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
import com.aheidelbacher.algostorm.engine.serialization.Deserializer.Companion.readValue
import com.aheidelbacher.algostorm.state.MapObject
import com.aheidelbacher.algostorm.state.Property
import com.aheidelbacher.algostorm.state.TileSet
import com.aheidelbacher.algostorm.systems.geometry2d.Point
import com.aheidelbacher.algostorm.systems.graphics2d.camera.Camera.Companion.CAMERA_X
import com.aheidelbacher.algostorm.systems.graphics2d.camera.Camera.Companion.CAMERA_Y

import com.aheidelbacher.algoventure.core.damage.HealthBarSystem.Companion.DAMAGEABLE_OBJECT_ID
import com.aheidelbacher.algoventure.core.generation.MapGenerator
import com.aheidelbacher.algoventure.core.generation.PrototypeObject
import com.aheidelbacher.algoventure.core.generation.PrototypeObject.Companion.createObject
import com.aheidelbacher.algoventure.core.generation.Random
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.ADJACENT_DIRECTIONS
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.DOOR
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.FLOOR
import com.aheidelbacher.algoventure.core.generation.dungeon.DungeonLevel.Companion.WALL
import com.aheidelbacher.algoventure.core.serialization.JsonSerializer
import com.aheidelbacher.algoventure.core.state.PLAYER_OBJECT_ID_PROPERTY
import com.aheidelbacher.algoventure.core.state.floor
import com.aheidelbacher.algoventure.core.state.healthBars
import com.aheidelbacher.algoventure.core.state.objectGroup

class DungeonMapGenerator(
        width: Int,
        height: Int,
        tileWidth: Int,
        tileHeight: Int,
        tileSets: List<String>,
        prototypes: List<String>,
        private val floorGid: List<Long>,
        private val wallMaskGid: Map<Int, List<Long>>
) : MapGenerator<DungeonLevel>(
        width = width,
        height = height,
        tileWidth = tileWidth,
        tileHeight = tileHeight,
        orientation = MapObject.Orientation.ORTHOGONAL,
        tileSets = tileSets.map { path ->
            getResourceStream(path).use { stream ->
                JsonSerializer.readValue<TileSet>(stream)
            }
        },
        prototypes = prototypes.associate { path ->
            val prototype = getResourceStream(path).use { stream ->
                JsonSerializer.readValue<PrototypeObject>(stream)
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
        fun newMap(playerPrototype: String): MapObject {
            val tiles = getResourceStream("/tile_sets.json").use {
                JsonSerializer.readValue<List<String>>(it)
            }
            val prototypes = getResourceStream("/prototypes.json").use {
                JsonSerializer.readValue<List<String>>(it)
            }
            return DungeonMapGenerator(
                    width = 32,
                    height = 32,
                    tileWidth = 24,
                    tileHeight = 24,
                    tileSets = tiles,
                    prototypes = prototypes,
                    floorGid = listOf(79, 79, 79, 79, 80),
                    wallMaskGid = mapOf(
                            0 to listOf(81L),
                            1 to listOf(87L),
                            2 to listOf(82L),
                            3 to listOf(90L),
                            4 to listOf(85L),
                            5 to listOf(86L, 86L, 86L, 86L, 97L),
                            6 to listOf(88L),
                            7 to listOf(95L),
                            8 to listOf(84L),
                            9 to listOf(91L),
                            10 to listOf(83L, 83L, 83L, 83L, 98L),
                            11 to listOf(96L),
                            12 to listOf(89L),
                            13 to listOf(94L),
                            14 to listOf(93L),
                            15 to listOf(92L)
                    )
            ).generate(playerPrototype)
        }
    }

    private val wallPrototype = this.prototypes["wall"]
            ?: error("Missing wall prototype!")
    private val doorPrototype = this.prototypes["door"]
            ?: error("Missing door prototype!")
    private val wallTorchPrototype = this.prototypes["wallTorch"]
            ?: error("Missing wall torch prototype!")
    private val skeletonPrototype = this.prototypes["skeleton"]
            ?: error("Missing skeleton prototype!")

    private fun generatePoint(width: Int, height: Int): Point = Point(
            x = (Math.random() * width).toInt(),
            y = (Math.random() * height).toInt()
    )

    override fun MapObject.decorate() {
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
            val obj = if (isPlayer) createObject(playerPrototype, x, y)
            else createObject(skeletonPrototype, x, y)
            objectGroup.add(obj)
            healthBars.add(create(
                    x = obj.x,
                    y = obj.y + obj.height - obj.height / 12,
                    width = obj.width,
                    height = obj.height / 12,
                    properties = mapOf(DAMAGEABLE_OBJECT_ID to Property(obj.id))
            ))
            if (isPlayer) {
                set(PLAYER_OBJECT_ID_PROPERTY, obj.id)
                set(CAMERA_X, obj.x + obj.width / 2)
                set(CAMERA_Y, obj.y + obj.height / 2)
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

    private fun MapObject.inflateTile(level: DungeonLevel, x: Int, y: Int) {
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
                val obj = createObject(
                        prototype = wallPrototype,
                        x = x * tileWidth,
                        y = y * tileHeight
                )
                obj.gid = gid
                objectGroup.add(obj)
                val canPlaceTorch = (mask and 4) == 0 && y + 1 < height &&
                        level[x, y + 1] == FLOOR
                if (canPlaceTorch && Random.nextInt(0, 100) < 10) {
                    objectGroup.add(createObject(
                            prototype = wallTorchPrototype,
                            x = x * tileWidth,
                            y = y * tileHeight
                    ))
                }
            }
            DOOR -> {
                objectGroup.add(createObject(
                        prototype = doorPrototype,
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

    override fun MapObject.inflateLevel(level: DungeonLevel) {
        for (y in 0 until level.height) {
            for (x in 0 until level.width) {
                inflateTile(level, x, y)
            }
        }
    }
}
