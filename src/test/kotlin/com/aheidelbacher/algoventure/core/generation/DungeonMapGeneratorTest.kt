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

import com.aheidelbacher.algostorm.engine.state.Layer

import org.junit.Test

class DungeonMapGeneratorTest {
    @Test
    fun testGenerator() {
        val map = DungeonMapGenerator.newMap("/prototypes/knight.json")
        for (y in 0 until map.height) {
            for (x in 0 until map.width) {
                val gid = (map.layers[0] as Layer.TileLayer)
                        .data[y * map.width + x]
                print(" $gid ")
            }
            print("\n")
        }
    }
}
