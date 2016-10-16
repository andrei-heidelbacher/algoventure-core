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

import com.aheidelbacher.algostorm.state.MapObject
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.state.Properties
import com.aheidelbacher.algostorm.state.Property

data class PrototypeObject(
        val name: String = "",
        val type: String = "",
        val width: Int,
        val height: Int,
        val isVisible: Boolean = true,
        val gid: Long = 0L,
        override val properties: Map<String, Property> = emptyMap()
) : Properties {
    companion object {
        fun MapObject.createObject(
                prototype: PrototypeObject,
                x: Int,
                y: Int
        ): Object = create(
                name = prototype.name,
                type = prototype.type,
                width = prototype.width,
                height = prototype.height,
                x = x,
                y = y,
                isVisible = prototype.isVisible,
                gid = prototype.gid,
                properties = prototype.properties
        )
    }

    init {
        require(width > 0 && height > 0)
        require(gid >= 0L)
    }
}
