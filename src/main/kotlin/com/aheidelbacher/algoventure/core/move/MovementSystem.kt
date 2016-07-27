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

package com.aheidelbacher.algoventure.core.move

import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber
import com.aheidelbacher.algostorm.physics2d.TransformIntent
import com.aheidelbacher.algostorm.physics2d.Transformed
import com.aheidelbacher.algostorm.state.ObjectManager

import com.aheidelbacher.algoventure.core.geometry2d.Direction

/**
 * This system handles the movement of entities.
 *
 * Upon receiving a [MoveIntent], it publishes a [TransformIntent], according to
 * the [Direction.dx] and [Direction.dy] translation amounts. Upon receiving a
 * [Transformed] event with translation amounts corresponding to a direction
 * (that is, `(1, -1)` for [Direction.NORTH_EAST], etc.), it publishes a [Moved]
 * event.
 */
class MovementSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher
) : Subscriber {
    @Subscribe fun handleMoveIntent(event: MoveIntent) {
        objectManager[event.objectId]?.let { obj ->
            publisher.post(TransformIntent(
                    objectId = obj.id,
                    dx = event.direction.dx,
                    dy = event.direction.dy,
                    rotate = 0F
            ))
        }
    }

    @Subscribe fun handleTransformed(event: Transformed) {
        objectManager[event.objectId]?.let { obj ->
            Direction.getDirection(event.dx, event.dy)?.let { direction ->
                publisher.post(Moved(obj.id, direction))
            }
        }
    }
}
