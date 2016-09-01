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

package com.aheidelbacher.algoventure.core.graphics2d

import com.aheidelbacher.algostorm.engine.Update
import com.aheidelbacher.algostorm.engine.tiled.Layer
import com.aheidelbacher.algostorm.engine.tiled.Layer.ObjectGroup.DrawOrder
import com.aheidelbacher.algostorm.engine.tiled.Object
import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import java.util.Comparator

class RenderOrderSystem(
        private val objectGroup: Layer.ObjectGroup,
        private val publisher: Publisher
) : Subscriber {
    companion object : Comparator<Object> {
        const val Z: String = "z"

        val Object.z: Int
            get() = getInt(Z) ?: -1

        override fun compare(o1: Object, o2: Object): Int =
                if (o1.y != o2.y) o1.y - o2.y else o1.z - o2.z
    }

    object SortObjects : Event

    init {
        require(objectGroup.drawOrder == DrawOrder.INDEX)
    }

    @Subscribe fun onSortObjects(event: SortObjects) {
        objectGroup.objects.sortWith(Companion)
    }

    @Subscribe fun onUpdate(event: Update) {
        publisher.post(SortObjects)
    }
}
