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

package com.aheidelbacher.algoventure.core.generation

import com.aheidelbacher.algostorm.engine.state.Object

import java.util.HashMap

data class PrototypeObject(
        val name: String = "",
        val type: String = "",
        val width: Int,
        val height: Int,
        val gid: Long = 0L,
        val visible: Boolean = true,
        val properties: Map<String, Any> = hashMapOf()
) {
    init {
        require(width > 0 && height > 0)
        require(gid >= 0L)
    }

    fun toObject(id: Int, x: Int, y: Int, rotation: Float = 0F): Object =
            Object(
                    name = name,
                    type = type,
                    id = id,
                    x = x,
                    y = y,
                    width = width,
                    height = height,
                    gid = gid,
                    rotation = rotation,
                    visible = visible,
                    properties = HashMap(properties)
            )
}
