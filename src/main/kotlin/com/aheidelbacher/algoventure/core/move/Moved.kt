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

package com.aheidelbacher.algoventure.core.move

import com.aheidelbacher.algoventure.core.act.ActionCompleted

import com.aheidelbacher.algoventure.core.geometry2d.Direction

/**
 * An event which signals that the given object moved in the specified
 * [direction].
 *
 * @property objectId the id of the moved object
 * @property direction the direction in which the object moved
 */
data class Moved(
        override val objectId: Int,
        val direction: Direction,
        override val usedStamina: Int
) : ActionCompleted

