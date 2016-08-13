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

import com.aheidelbacher.algostorm.engine.state.Object

object Health {
    const val HEALTH: String = "health"
    const val MAX_HEALTH: String = "maxHealth"

    val Object.isDamageable: Boolean
        get() = contains(HEALTH) && contains(MAX_HEALTH)

    val Object.health: Int
        get() = get(HEALTH) as Int?
                ?: error("Object $id must contain $HEALTH property!")

    val Object.maxHealth: Int
        get() = get(MAX_HEALTH) as Int?
                ?: error("Object $id must contain $MAX_HEALTH property!")

    fun Object.applyDamage(damage: Int) {
        set(HEALTH, Math.max(0, health - damage))
    }
}
