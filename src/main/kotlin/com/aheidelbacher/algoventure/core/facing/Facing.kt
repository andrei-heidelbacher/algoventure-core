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

package com.aheidelbacher.algoventure.core.facing

import com.aheidelbacher.algostorm.engine.state.Object

enum class Facing {
    LEFT, RIGHT;

    companion object {
        /**
         * The name of the facing property.
         */
        const val PROPERTY: String = "facing"

        /**
         * The [Facing] of this entity, or `null` if it doesn't have a facing.
         */
        val Object.facing: Facing?
            get() = properties[PROPERTY] as Facing?
    }
}
