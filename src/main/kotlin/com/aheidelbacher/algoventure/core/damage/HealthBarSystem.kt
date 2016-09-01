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

import com.aheidelbacher.algostorm.engine.Update
import com.aheidelbacher.algostorm.engine.tiled.Layer
import com.aheidelbacher.algostorm.engine.tiled.Object
import com.aheidelbacher.algostorm.engine.tiled.ObjectManager
import com.aheidelbacher.algostorm.engine.tiled.Properties.PropertyType
import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.damage.DamageSystem.Companion.health
import com.aheidelbacher.algoventure.core.damage.DamageSystem.Companion.maxHealth

class HealthBarSystem(
        private val objectManager: ObjectManager,
        private val healthBarsObjectGroup: Layer.ObjectGroup,
        private val publisher: Publisher
) : Subscriber {
    companion object {
        const val DAMAGEABLE_OBJECT_ID: String = "damageableObjectId"

        val Object.damageableObjectId: Int
            get() = getInt(DAMAGEABLE_OBJECT_ID)
                    ?: error("Health bar $id must have $DAMAGEABLE_OBJECT_ID!")
    }
    object UpdateHealthBars : Event

    @Subscribe fun onUpdateHeatlhBars(event: UpdateHealthBars) {
        val toRemove = mutableListOf<Object>()
        healthBarsObjectGroup.objects.forEach { healthBar ->
            objectManager[healthBar.damageableObjectId]?.let { obj ->
                healthBar.x = obj.x
                healthBar.y = obj.y + obj.height - healthBar.height
                val newWidth = (1F * obj.width * obj.health / obj.maxHealth)
                        .toInt()
                if (newWidth != healthBar.width) {
                    objectManager.create(
                            x = healthBar.x,
                            y = healthBar.y,
                            width = newWidth,
                            height = healthBar.height,
                            properties = healthBar.properties,
                            propertyTypes = healthBar.propertyTypes
                    )
                    objectManager.delete(healthBar.id)
                }
            } ?: toRemove.add(healthBar)
        }
        toRemove.forEach { healthBarsObjectGroup.objects.remove(it) }
    }

    @Subscribe fun onUpdate(event: Update) {
        publisher.post(UpdateHealthBars)
    }
}
