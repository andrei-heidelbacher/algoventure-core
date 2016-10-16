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

import com.aheidelbacher.algostorm.systems.geometry2d.Point

/**
 * A digitalized line in a 2D grid from one point to another. Uses the
 * super-cover variant of [Bresenham's line algorithm][superCoverBresenham].
 *
 * For example, a line from `(0, 0)` to `(7, 5)` would be represented as the
 * following cells:
 * `
 * ......##
 * .....##.
 * ....##..
 * ..##....
 * .##.....
 * ##......
 * `
 */
data class LineOfSight(val from: Point, val to: Point) {
    private companion object {
        /**
         * Octants in which the plane is split:
         * `
         * \2|1/
         * 3\|/0
         * --+--
         * 4/|\7
         * /5|6\
         * `
         */
        enum class Octant {
            ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN
        }

        /**
         * The octant in which this point is located.
         */
        val Point.octant: Octant
            get() = when {
                x >= 0 && y >= 0 && x >= y -> Octant.ZERO
                x >= 0 && y >= 0 && x < y -> Octant.ONE
                x < 0 && y >= 0 && -x <= y -> Octant.TWO
                x < 0 && y >= 0 && -x > y -> Octant.THREE
                x < 0 && y < 0 && x <= y -> Octant.FOUR
                x < 0 && y < 0 && x > y -> Octant.FIVE
                x >= 0 && y < 0 && x <= -y -> Octant.SIX
                x >= 0 && y < 0 && x > -y -> Octant.SEVEN
                else -> throw AssertionError("Point must be inside an octant!")
            }

        /**
         * Rotates this point from its current [octant] to [Octant.ZERO].
         *
         * @return the rotated point
         */
        fun Point.switchToOctantZero(): Point = when (octant) {
            Octant.ZERO -> Point(x, y)
            Octant.ONE -> Point(y, x)
            Octant.TWO -> Point(y, -x)
            Octant.THREE -> Point(-x, y)
            Octant.FOUR -> Point(-x, -y)
            Octant.FIVE -> Point(-y, -x)
            Octant.SIX -> Point(-y, x)
            Octant.SEVEN -> Point(x, -y)
        }

        /**
         * Rotates this point (currently placed in [Octant.ZERO]) to the given
         * [octant].
         *
         * @param octant the destination octant
         * @return the rotated point
         * @throws IllegalArgumentException if this point is not in
         * [Octant.ZERO].
         */
        fun Point.switchFromOctantZeroTo(octant: Octant): Point {
            require(this.octant == Octant.ZERO) {
                "Point is not in octant zero!"
            }
            return when (octant) {
                Octant.ZERO -> Point(x, y)
                Octant.ONE -> Point(y, x)
                Octant.TWO -> Point(-y, x)
                Octant.THREE -> Point(-x, y)
                Octant.FOUR -> Point(-x, -y)
                Octant.FIVE -> Point(-y, -x)
                Octant.SIX -> Point(y, -x)
                Octant.SEVEN -> Point(x, -y)
            }
        }

        /**
         * The super-cover variant of Bresenham's algorithm which draws a line
         * from the origin `(0, 0)` to the given [point]; only works if the
         * given point is in [Octant.ZERO] of the plane.
         *
         * @param point the end-point of the line
         * @return the digitalized line
         * @throws IllegalArgumentException if the given [point] is not in
         * [Octant.ZERO]
         */
        fun superCoverBresenham(point: Point): List<Point> {
            require(point.octant == Octant.ZERO) {
                "Point must be in octant zero!"
            }
            val a = 2L * point.y
            val b = -2L * point.x
            val line = mutableListOf<Point>()

            tailrec fun step(x: Int, y: Int) {
                line.add(Point(x, y))
                if (Point(x, y) != point) {
                    if (Math.abs(a * (x + 1) + b * y) < Math.abs(a * (x + 1) + b * (y + 1))) {
                        step(x + 1, y)
                    } else {
                        val d = a * x + a / 2 + b * y + b / 2
                        if (d < 0)
                            line.add(Point(x + 1, y))
                        else if (d > 0)
                            line.add(Point(x, y + 1))
                        step(x + 1, y + 1)
                    }
                }
            }

            step(0, 0)
            return line
        }
    }

    /**
     * The cells contained by this digitalized line.
     */
    val line: List<Point>
        get() = superCoverBresenham((to - from).switchToOctantZero()).map {
            it.switchFromOctantZeroTo((to - from).octant) + from
        }
}
