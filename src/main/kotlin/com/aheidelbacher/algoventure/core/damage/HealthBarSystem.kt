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

import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber
import com.aheidelbacher.algostorm.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.systems.Update

import com.aheidelbacher.algoventure.core.damage.DamageSystem.Companion.health
import com.aheidelbacher.algoventure.core.damage.DamageSystem.Companion.maxHealth

class HealthBarSystem(
        private val objectFactory: Object.Factory,
        private val objectGroup: ObjectGroup,
        private val healthBarsObjectGroup: ObjectGroup,
        private val publisher: Publisher
) : Subscriber {
    companion object {
        const val DAMAGEABLE_OBJECT_ID: String = "damageableObjectId"

        val Object.damageableObjectId: Int
            get() = getInt(DAMAGEABLE_OBJECT_ID)
                    ?: error("Health bar $id must have $DAMAGEABLE_OBJECT_ID!")
    }

    object UpdateHealthBars : Event

    @Subscribe fun onUpdateHealthBars(event: UpdateHealthBars) {
        healthBarsObjectGroup.objectSet.toList().forEach { healthBar ->
            objectGroup[healthBar.damageableObjectId]?.let { obj ->
                healthBar.x = obj.x
                healthBar.y = obj.y + healthBar.height
                val newWidth = (1F * obj.width * obj.health / obj.maxHealth)
                        .toInt()
                if (newWidth != healthBar.width) {
                    healthBarsObjectGroup.remove(healthBar.id)
                    healthBarsObjectGroup.add(objectFactory.create(
                            x = healthBar.x,
                            y = healthBar.y,
                            width = newWidth,
                            height = healthBar.height,
                            properties = healthBar.properties
                    ))
                }
            } ?: healthBarsObjectGroup.remove(healthBar.id)
        }
    }

    @Subscribe fun onUpdate(event: Update) {
        publisher.post(UpdateHealthBars)
    }
}
