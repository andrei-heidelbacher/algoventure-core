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

package com.aheidelbacher.algoventure.core.state

import com.aheidelbacher.algostorm.engine.tiled.Layer
import com.aheidelbacher.algostorm.engine.tiled.Layer.ObjectGroup.DrawOrder
import com.aheidelbacher.algostorm.engine.tiled.Map
import com.aheidelbacher.algostorm.engine.tiled.Properties.Color

object State {
    const val FLOOR_TILE_LAYER_NAME: String = "floor"
    const val OBJECT_GROUP_NAME: String = "objects"
    const val HEALTH_BAR_OBJECT_GROUP_NAME: String = "healthBars"
    const val FOG_OF_WAR: String = "fogOfWar"
    const val PLAYER_OBJECT_ID_PROPERTY: String = "playerId"

    val Map.isValid: Boolean
        get() = PLAYER_OBJECT_ID_PROPERTY in properties &&
                layers.size == 3 &&
                layers[0] is Layer.TileLayer &&
                layers[0].name == FLOOR_TILE_LAYER_NAME &&
                layers[1] is Layer.ObjectGroup &&
                layers[1].name == OBJECT_GROUP_NAME &&
                (layers[1] as Layer.ObjectGroup).drawOrder == DrawOrder.INDEX &&
                layers[2].name == HEALTH_BAR_OBJECT_GROUP_NAME &&
                layers[2] is Layer.ObjectGroup &&
                (layers[2] as Layer.ObjectGroup).drawOrder == DrawOrder.INDEX &&
                (layers[2] as Layer.ObjectGroup).color == Color("#ffff0000")

    val Map.floor: Layer.TileLayer
        get() = layers[0] as Layer.TileLayer

    val Map.objectGroup: Layer.ObjectGroup
        get() = layers[1] as Layer.ObjectGroup

    val Map.healthBars: Layer.ObjectGroup
        get() = layers[2] as Layer.ObjectGroup

    val Map.fogOfWar: Layer.TileLayer
        get() = layers[3] as Layer.TileLayer

    val Map.playerObjectId: Int
        get() = properties[PLAYER_OBJECT_ID_PROPERTY] as Int?
                ?: error("Missing $PLAYER_OBJECT_ID_PROPERTY property!")
}
