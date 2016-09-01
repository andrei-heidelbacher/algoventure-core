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

package com.aheidelbacher.algoventure.core.damage

import com.aheidelbacher.algostorm.engine.physics2d.PhysicsSystem.Companion.intersects
import com.aheidelbacher.algostorm.engine.tiled.Object
import com.aheidelbacher.algostorm.engine.tiled.ObjectManager
import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

class DamageSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher
) : Subscriber {
    companion object {
        const val HEALTH: String = "health"
        const val MAX_HEALTH: String = "maxHealth"

        val Object.isDamageable: Boolean
            get() = contains(HEALTH) && contains(MAX_HEALTH)

        val Object.health: Int
            get() = getInt(HEALTH)
                    ?: error("Object $id must contain $HEALTH property!")

        val Object.maxHealth: Int
            get() = getInt(MAX_HEALTH)
                    ?: error("Object $id must contain $HEALTH property!")

        fun Object.addHealth(amount: Int) {
            set(HEALTH, Math.min(maxHealth, Math.max(0, health + amount)))
        }

        val Object.isDead: Boolean
            get() = health == 0
    }

    data class Damage(
            val damage: Int,
            val x: Int,
            val y: Int,
            val width: Int,
            val height: Int
    ) : Event {
        init {
            require(width > 0 && height > 0) {
                "Damage area sizes ($width, $height) must be positive!"
            }
        }
    }

    data class DeleteEntity(val objectId: Int) : Event

    @Subscribe fun onDamage(event: Damage) {
        objectManager.objects.filter {
            it.intersects(event.x, event.y, event.width, event.height)
                    && it.isDamageable
        }.forEach { obj ->
            obj.addHealth(-event.damage)
            if (obj.isDead) {
                publisher.post(Death(obj.id))
            }
        }
    }

    @Subscribe fun onDeath(event: Death) {
        publisher.post(DeleteEntity(event.objectId))
    }

    @Subscribe fun onDeleteEntity(event: DeleteEntity) {
        objectManager.delete(event.objectId)
    }
}
