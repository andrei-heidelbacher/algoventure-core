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

package com.aheidelbacher.algoventure.core.ai

//import com.aheidelbacher.algostorm.engine.physics2d.Rigid.isRigid
import com.aheidelbacher.algostorm.engine.geometry2d.Point
import com.aheidelbacher.algostorm.engine.geometry2d.Rectangle
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algoventure.core.geometry2d.Direction
import java.util.Comparator
import java.util.PriorityQueue

object Util {
    private const val INFINITY = 0x0ffffff
    private fun getRigid(
            width: Int,
            height: Int,
            tileWidth: Int,
            tileHeight: Int,
            objectManager: ObjectManager
    ): BooleanArray {
        val isRigid = BooleanArray(width * height)
        objectManager.objects.forEach {
            //if (it.isRigid) {

            //}
        }
        return isRigid
    }

    private fun findPath(
            isRigid: BooleanArray,
            width: Int,
            height: Int,
            source: Point,
            destination: Point
    ): List<Direction>? {
        val visited = hashSetOf<Point>()
        val father = hashMapOf<Point, Direction>()
        val gScore = hashMapOf(source to 0)
        val fScore = hashMapOf(source to 0)
        gScore[source]
        val heap = PriorityQueue<Point>(Comparator<Point> { o1, o2 ->
            (fScore[o1] ?: INFINITY) - (fScore[o2] ?: INFINITY)
        })
        heap.add(source)
        while (heap.isNotEmpty()) {
            val top = heap.poll()
            for (d in Direction.values()) {
                val neighbor = top.translate(d.dx, d.dy)
            }
        }
        return null
    }
}
