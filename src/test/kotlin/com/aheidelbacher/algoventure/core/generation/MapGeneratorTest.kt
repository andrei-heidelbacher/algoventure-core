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

import com.aheidelbacher.algostorm.engine.serialization.Serializer
import com.aheidelbacher.algoventure.core.facing.Facing
import org.junit.Test
import java.io.ByteArrayOutputStream

class MapGeneratorTest {
    @Test
    fun testGenerator() {
        val map = MapGenerator.newMap("/prototypes/knight.json")
    }

    @Test
    fun testFacingSerialization() {
        val bos = ByteArrayOutputStream()
        val props = mapOf<String, Any>("facing" to Facing.LEFT)
        Serializer.writeValue(bos, props)
        println(bos.toString())
    }
}
