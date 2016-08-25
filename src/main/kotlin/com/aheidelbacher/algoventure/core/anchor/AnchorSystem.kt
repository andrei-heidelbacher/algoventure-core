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

package com.aheidelbacher.algoventure.core.anchor

import com.aheidelbacher.algostorm.engine.Update
import com.aheidelbacher.algostorm.engine.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

class AnchorSystem(
        private val anchoredObjectGroup: ObjectGroup,
        private val anchorObjectManager: ObjectManager,
        private val publisher: Publisher
) : Subscriber {
    companion object {
        const val ANCHOR_OBJECT_ID: String = "anchorObjectId"
        const val ANCHOR_OFFSET_X: String = "anchorOffsetX"
        const val ANCHOR_OFFSET_Y: String = "anchorOffsetY"

        val Object.isAnchored: Boolean
            get() = contains(ANCHOR_OBJECT_ID)

        val Object.anchorObjectId: Int
            get() = get(ANCHOR_OBJECT_ID) as Int? ?: error(
                    "Object $id must contain $ANCHOR_OBJECT_ID property!"
            )

        val Object.anchorOffsetX: Int
            get() = get(ANCHOR_OFFSET_X) as Int? ?: 0

        val Object.anchorOffsetY: Int
            get() = get(ANCHOR_OFFSET_Y) as Int? ?: 0
    }

    object UpdateAnchors : Event

    @Subscribe fun onUpdateAnchors(event: UpdateAnchors) {
        val toRemove = arrayListOf<Object>()
        anchoredObjectGroup.objects.forEach { obj ->
            if (obj.isAnchored) {
                anchorObjectManager[obj.anchorObjectId]?.let { anchorObj ->
                    obj.x = anchorObj.x + obj.anchorOffsetX
                    obj.y = anchorObj.y + obj.anchorOffsetY
                } ?: toRemove.add(obj)
            }
        }
        toRemove.forEach { anchoredObjectGroup.objects.remove(it) }
    }

    @Subscribe fun onUpdate(event: Update) {
        publisher.post(UpdateAnchors)
    }
}
