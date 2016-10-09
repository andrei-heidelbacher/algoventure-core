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

package com.aheidelbacher.algoventure.core.attack

import com.aheidelbacher.algostorm.engine.physics2d.Collision
import com.aheidelbacher.algostorm.engine.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.damage.DamageSystem.Companion.isDamageable
import com.aheidelbacher.algoventure.core.damage.DamageSystem.Damage
import com.aheidelbacher.algoventure.core.geometry2d.Direction

class AttackSystem(
        private val tileWidth: Int,
        private val tileHeight: Int,
        private val objectGroup: ObjectGroup,
        private val publisher: Publisher
) : Subscriber {
    @Subscribe fun onCollision(event: Collision) {
        val attacker = objectGroup[event.sourceId]
        val defender = objectGroup[event.targetId]
        if (attacker != null && defender != null && defender.isDamageable) {
            Direction.getDirection(
                    dx = defender.x - attacker.x,
                    dy = defender.y - attacker.y
            )?.let { direction ->
                publisher.post(Attacked(attacker.id, direction, 100))
            }
        }
    }

    @Subscribe fun onAttacked(event: Attacked) {
        // TODO: fix the damage location
        objectGroup[event.objectId]?.let { obj ->
            publisher.post(Damage(
                    damage = 25,
                    x = obj.x + event.direction.dx * tileWidth,
                    y = obj.y + event.direction.dy * tileHeight,
                    width = tileWidth,
                    height = tileHeight
            ))
        }
    }
}
