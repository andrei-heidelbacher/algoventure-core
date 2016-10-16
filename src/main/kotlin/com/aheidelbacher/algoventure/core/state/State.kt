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

@file:JvmName("Util")

package com.aheidelbacher.algoventure.core.state

import com.aheidelbacher.algostorm.engine.serialization.Deserializer.Companion.readValue
import com.aheidelbacher.algostorm.state.Color
import com.aheidelbacher.algostorm.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.state.Layer.ObjectGroup.DrawOrder
import com.aheidelbacher.algostorm.state.Layer.TileLayer
import com.aheidelbacher.algostorm.state.MapObject
import com.aheidelbacher.algostorm.state.MutableProperties
import com.aheidelbacher.algostorm.state.Properties

import com.aheidelbacher.algoventure.core.serialization.JsonSerializer

import java.io.ByteArrayOutputStream

const val FLOOR_TILE_LAYER_NAME: String = "floor"
const val OBJECT_GROUP_NAME: String = "objects"
const val HEALTH_BAR_OBJECT_GROUP_NAME: String = "healthBars"
const val FOG_OF_WAR: String = "fogOfWar"
const val PLAYER_OBJECT_ID_PROPERTY: String = "playerId"

inline fun <reified T : Any> Properties.get(name: String): T? =
        getString(name)?.byteInputStream()?.let { JsonSerializer.readValue(it) }

inline fun <reified T : Any> MutableProperties.set(name: String, value: T) {
    val stream = ByteArrayOutputStream()
    JsonSerializer.writeValue(stream, value)
    set(name, stream.toString())
}

val MapObject.isValid: Boolean
    get() = PLAYER_OBJECT_ID_PROPERTY in properties &&
            layers.size == 3 &&
            layers[0] is TileLayer &&
            layers[0].name == FLOOR_TILE_LAYER_NAME &&
            layers[1] is ObjectGroup &&
            layers[1].name == OBJECT_GROUP_NAME &&
            (layers[1] as ObjectGroup).drawOrder == DrawOrder.INDEX &&
            layers[2].name == HEALTH_BAR_OBJECT_GROUP_NAME &&
            layers[2] is ObjectGroup &&
            (layers[2] as ObjectGroup).drawOrder == DrawOrder.INDEX &&
            (layers[2] as ObjectGroup).color == Color("#ffff0000")

val MapObject.floor: TileLayer
    get() = layers[0] as TileLayer

val MapObject.objectGroup: ObjectGroup
    get() = layers[1] as ObjectGroup

val MapObject.healthBars: ObjectGroup
    get() = layers[2] as ObjectGroup

val MapObject.fogOfWar: TileLayer
    get() = layers[3] as TileLayer

val MapObject.playerObjectId: Int
    get() = getInt(PLAYER_OBJECT_ID_PROPERTY)
            ?: error("Missing $PLAYER_OBJECT_ID_PROPERTY property!")
