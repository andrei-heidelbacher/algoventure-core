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
import com.aheidelbacher.algostorm.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.systems.physics2d.PhysicsSystem.TransformIntent
import com.aheidelbacher.algostorm.systems.physics2d.Transformed

import com.aheidelbacher.algoventure.core.act.Action
import com.aheidelbacher.algoventure.core.geometry2d.Direction

/**
 * This system handles the movement of entities.
 *
 * Upon receiving an [Action.Move], it publishes a [TransformIntent], according
 * to the [Direction.dx] and [Direction.dy] translation amounts. Upon receiving
 * a [Transformed] event with translation amounts corresponding to a direction
 * (that is, `(1, -1)` for [Direction.NORTH_EAST], etc.), it publishes a [Moved]
 * event.
 */
class MovementSystem(
        private val tileWidth: Int,
        private val tileHeight: Int,
        private val objectGroup: ObjectGroup,
        private val publisher: Publisher
) : Subscriber {
    @Subscribe fun onMove(event: Action.Move) {
        objectGroup[event.objectId]?.let { obj ->
            publisher.post(TransformIntent(
                    objectId = obj.id,
                    dx = event.direction.dx * tileWidth,
                    dy = event.direction.dy * tileHeight
            ))
        }
    }

    @Subscribe fun onWait(event: Action.Wait) {
        objectGroup[event.objectId]?.let { obj ->
            publisher.post(Waited(obj.id, 100))
        }
    }

    @Subscribe fun onTransformed(event: Transformed) {
        objectGroup[event.objectId]?.let { obj ->
            Direction.getDirection(event.dx, event.dy)?.let { direction ->
                publisher.post(Moved(obj.id, direction, 100))
            }
        }
    }
}
