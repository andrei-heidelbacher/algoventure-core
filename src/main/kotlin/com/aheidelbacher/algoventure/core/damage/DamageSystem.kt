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

import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.damage.Damageable.Companion.damageable

class DamageSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher
) : Subscriber {
    data class DeleteEntity(val objectId: Int) : Event

    @Subscribe fun handleDamage(event: Damage) {
        objectManager.objects.filter {
            true
        }.forEach { obj ->
            obj.damageable?.let { damageable ->
                val newDamageable = damageable.applyDamage(event.damage)
                obj.properties[Damageable.PROPERTY] = newDamageable
                if (newDamageable.isDead) {
                    publisher.post(Death(obj.id))
                }
            }
        }
    }

    @Subscribe fun handleDeath(event: Death) {
        publisher.post(DeleteEntity(event.objectId))
    }

    @Subscribe fun handleDeleteEntity(event: DeleteEntity) {
        objectManager.delete(event.objectId)
    }
}
