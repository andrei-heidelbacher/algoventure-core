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

@file:JvmName("Util")

package com.aheidelbacher.algoventure.core.ai

import com.aheidelbacher.algostorm.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.systems.physics2d.PhysicsSystem.Companion.isRigid
import com.aheidelbacher.algostorm.systems.geometry2d.Point

import com.aheidelbacher.algoventure.core.geometry2d.Direction

import java.util.PriorityQueue

private fun findPath(
        source: Point,
        destination: Point,
        isRigid: (Point) -> Boolean
): List<Direction>? {
    data class HeapNode(val p: Point, val f: Int) : Comparable<HeapNode> {
        override fun compareTo(other: HeapNode): Int = f - other.f
    }

    fun hScore(p : Point): Int = Math.max(
            Math.abs(p.x - destination.x),
            Math.abs(p.y - destination.y)
    )

    val INF = 0x0fffffff

    val visited = hashSetOf<Point>()
    val father = hashMapOf<Point, Direction>()
    val gScore = hashMapOf(source to 0)
    val fScore = hashMapOf(source to hScore(source))
    val heap = PriorityQueue<HeapNode>()
    heap.add(HeapNode(source, fScore[source] ?: INF))
    while (heap.isNotEmpty()) {
        val v = heap.poll().p
        if (v == destination) {
            val path = arrayListOf<Direction>()
            var head = destination
            while (head in father) {
                father[head]?.let { d ->
                    path.add(d)
                    head = head.translate(-d.dx, -d.dy)
                }
            }
            path.reverse()
            return path
        }
        val vCost = gScore[v] ?: INF
        visited.add(v)
        for (d in Direction.values()) {
            val w = v.translate(d.dx, d.dy)
            val wCost = gScore[w] ?: INF
            if (!isRigid(w) && w !in visited && vCost + 1 < wCost) {
                gScore[w] = vCost + 1
                father[w] = d
                heap.add(HeapNode(w, vCost + 1 + hScore(w)))
            }
        }
    }
    return null
}

fun findPath(
        objectGroup: ObjectGroup,
        tileWidth: Int,
        tileHeight: Int,
        source: Point,
        destination: Point
): List<Direction>? {
    val rigid = hashSetOf<Point>()
    for (obj in objectGroup.objectSet) {
        if (obj.isRigid) {
            rigid.add(Point(obj.x / tileWidth, obj.y / tileHeight))
        }
    }
    return findPath(source, destination) { it in rigid }
}
