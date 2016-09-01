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

package com.aheidelbacher.algoventure.core.vision

import com.aheidelbacher.algostorm.engine.tiled.Object

object Vision {
    const val SIGHT_RADIUS: String = "sightRadius"
    const val IS_OPAQUE: String = "isOpaque"

    val Object.sightRadius: Int
        get() = getInt(SIGHT_RADIUS)
                ?: error("Object $id must have $SIGHT_RADIUS property!")

    val Object.isOpaque: Boolean
        get() = getBoolean(IS_OPAQUE) ?: false
}
