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

import com.aheidelbacher.algostorm.engine.state.Layer
import com.aheidelbacher.algostorm.engine.state.Layer.ObjectGroup.DrawOrder
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

class RenderOrderSystem(
        private val objectGroup: Layer.ObjectGroup
) : Subscriber {
    init {
        require(objectGroup.drawOrder == DrawOrder.INDEX)
    }

    @Subscribe fun onSortObjects(event: SortObjects) {
        val objects = objectGroup.objects.toTypedArray()
        objects.sortWith(RenderOrder)
        objectGroup.objects.clear()
        objectGroup.objects.addAll(objects)
    }
}
