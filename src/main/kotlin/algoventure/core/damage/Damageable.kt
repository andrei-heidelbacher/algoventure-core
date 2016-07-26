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

package algoventure.core.damage

import algostorm.state.Object

data class Damageable(
        val maxHealth: Int,
        val health: Int
) {
    companion object {
        const val PROPERTY: String = "damageable"

        val Object.damageable: Damageable?
            get() = properties[PROPERTY] as Damageable?
    }

    init {
        require(maxHealth > 0) { "Maximum health must be positive!" }
        require(health >= 0) { "Health can't be negative!" }
        require(health <= maxHealth) { "Health can't exceed maximum health!" }
    }

    val isDead: Boolean
        get() = health == 0

    fun applyDamage(damage: Int): Damageable = copy(
            health = Math.max(health - damage, 0)
    )
}
