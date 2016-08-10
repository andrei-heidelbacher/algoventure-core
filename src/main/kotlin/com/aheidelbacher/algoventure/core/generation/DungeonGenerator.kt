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

import com.aheidelbacher.algoventure.core.geometry2d.Point
import com.aheidelbacher.algoventure.core.geometry2d.Rectangle

object DungeonGenerator {
    enum class Tile {
        FLOOR, WALL, DOOR, STAIRS_UP, STAIRS_DOWN, EMPTY
    }

    sealed class Tree(val area: Rectangle) {
        class Node(area: Rectangle, val left: Tree, val right: Tree) : Tree(area)
        class Leaf(area: Rectangle) : Tree(area)
    }

    fun generateBSP(area: Rectangle, maxSize: Int): Tree {
        if (area.width <= maxSize && area.height <= maxSize) {
            return Tree.Leaf(area)
        } else {
            if (area.width < area.height) {
                val top = Rectangle(
                        x = area.x,
                        y = area.y,
                        width = area.width,
                        height = area.height / 2
                )
                val bottom = Rectangle(
                        x = area.x,
                        y = area.y + area.height / 2,
                        width = area.width,
                        height = area.height - area.height / 2
                )
                return Tree.Node(
                        left = generateBSP(bottom, maxSize),
                        right = generateBSP(top, maxSize),
                        area = area
                )
            } else {
                val left = Rectangle(
                        x = area.x,
                        y = area.y,
                        width = area.width / 2,
                        height = area.height
                )
                val right = Rectangle(
                        x = area.x + area.width / 2,
                        y = area.y,
                        width = area.width - area.width / 2,
                        height = area.height
                )
                return Tree.Node(
                        left = generateBSP(left, maxSize),
                        right = generateBSP(right, maxSize),
                        area = area
                )
            }
        }
    }

    fun isOnBorder(area: Rectangle, x: Int, y: Int): Boolean =
            x == area.x || x == area.x + area.width - 1 ||
                    y == area.y || y == area.y + area.height - 1

    fun buildDungeon(tree: Tree): MutableMap<Point, Tile> = when (tree) {
        is Tree.Leaf -> {
            val room = hashMapOf<Point, Tile>()
            for (x in 0 until tree.area.width) {
                for (y in 0 until tree.area.height) {
                    room[Point(tree.area.x + x, tree.area.y + y)] =
                            if (isOnBorder(tree.area, x, y)) Tile.WALL
                            else Tile.FLOOR
                }
            }
            room
        }
        is Tree.Node -> {
            val dungeon = hashMapOf<Point, Tile>()
            dungeon.putAll(buildDungeon(tree.left))
            dungeon.putAll(buildDungeon(tree.right))
            val leftCenter = Point(
                    x = tree.left.area.x + tree.left.area.width / 2,
                    y = tree.left.area.y + tree.left.area.height / 2
            )
            val rightCenter = Point(
                    x = tree.right.area.x + tree.right.area.width / 2,
                    y = tree.right.area.y + tree.right.area.height / 2
            )
            for (x in (leftCenter.x)..(rightCenter.x))
                dungeon[Point(x, leftCenter.y)] = Tile.FLOOR
            for (y in (leftCenter.y)..(rightCenter.y))
                dungeon[Point(rightCenter.x, y)] = Tile.FLOOR
            dungeon
        }
    }

    fun generate(width: Int, height: Int, maxSize: Int): Map<Point, Tile> =
            buildDungeon(generateBSP(Rectangle(0, 0, width, height), maxSize))
}
