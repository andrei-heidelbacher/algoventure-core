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

import com.aheidelbacher.algostorm.engine.geometry2d.Point
import com.aheidelbacher.algostorm.engine.geometry2d.Circle

/**
 * A field of vision in the shape of a circle centered on a source point,
 * knowing whether a cell is [blocked] or not.
 */
data class FieldOfVision(val field: Circle, val blocked: (Point) -> Boolean) {
    /**
     * Returns whether the given [point] is visible from the source or not.
     *
     * @param point the point which should be checked for visibility
     * @return `true` if the given point is visible, `false` otherwise
     */
    operator fun contains(point: Point): Boolean {
        val center = Point(field.x, field.y)
        return point in field && LineOfSight(center, point).line.none {
            it != center && it != point && blocked(it)
        }
    }
}
