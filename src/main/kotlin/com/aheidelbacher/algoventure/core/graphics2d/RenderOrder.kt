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

package com.aheidelbacher.algoventure.core.graphics2d

import com.aheidelbacher.algostorm.engine.state.Object

import java.util.Comparator

object RenderOrder : Comparator<Object> {
    const val Z: String = "z"

    val Object.z: Int
        get() = get(Z) as Int? ?: -1

    override fun compare(o1: Object, o2: Object): Int =
            if (o1.y != o2.y) o1.y - o2.y
            else if (o1.z != o2.z) o1.z - o2.z
            else o1.x - o2.x
}
