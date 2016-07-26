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

package algoventure.core.attack

import algostorm.event.Publisher
import algostorm.event.Subscribe
import algostorm.event.Subscriber
import algostorm.physics2d.Collision
import algostorm.state.ObjectManager

import algoventure.core.damage.Damage
import algoventure.core.damage.Damageable.Companion.damageable
import algoventure.core.geometry2d.Direction

class AttackSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher,
        private val tileWidth: Int,
        private val tileHeight: Int
) : Subscriber {
    @Subscribe fun handleCollision(event: Collision) {
        val attacker = objectManager[event.sourceId]
        val defender = objectManager[event.targetId]
        val damageable = defender?.damageable
        if (attacker != null && defender != null && damageable != null) {
            Direction.getDirection(
                    dx = defender.x - attacker.x,
                    dy = defender.y - attacker.y
            )?.let { direction ->
                publisher.post(Attacked(attacker.id, direction))
            }
        }
    }

    @Subscribe fun handleAttacked(event: Attacked) {
        objectManager[event.objectId]?.let { obj ->
            publisher.post(Damage(
                    damage = 10,
                    x = obj.x + event.direction.dx,
                    y = obj.y + event.direction.dy,
                    width = tileWidth,
                    height = tileHeight
            ))
        }
    }
}
