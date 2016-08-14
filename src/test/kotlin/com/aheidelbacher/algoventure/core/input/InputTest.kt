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

package com.aheidelbacher.algoventure.core.input

import com.aheidelbacher.algostorm.engine.serialization.Serializer

import org.junit.Assert.assertEquals
import org.junit.Test

import java.io.ByteArrayOutputStream

class InputTest {
    @Test
    fun testInputPropertySerialization() {
        val props = mutableMapOf<String, Any>()
        val click = Input.Click(-5, 67)
        props[Input.INPUT] = click
        val bos = ByteArrayOutputStream()
        Serializer.writeValue(bos, props)
        println(bos.toString())
        val bis = bos.toByteArray().inputStream()
        val deserializedClick = Serializer
                .readValue<Map<String, Any>>(bis)[Input.INPUT] as Input.Click?
                ?: error("Missing input property after deserialization!")
        assertEquals(click.x, deserializedClick.x)
        assertEquals(click.y, deserializedClick.y)
    }
}
