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

package com.aheidelbacher.algoventure.core.vision

import com.aheidelbacher.algostorm.engine.geometry2d.Point
import com.aheidelbacher.algostorm.engine.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.engine.state.Layer.TileLayer
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber
import com.aheidelbacher.algoventure.core.move.Moved

class VisionSystem(
        private val tileWidth: Int,
        private val tileHeight: Int,
        private val objectGroup: ObjectGroup,
        private val fogOfWarLayer: TileLayer
) : Subscriber {
    private var visibleTiles: List<Point> = emptyList()

    init {
        updateFogOfWar()
    }

    private fun updateFogOfWar() {
        visibleTiles.forEach {
            val (x, y) = it
        }
    }

    @Subscribe fun onMoved(event: Moved) {

    }
}
