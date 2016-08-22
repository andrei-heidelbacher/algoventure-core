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

import com.aheidelbacher.algostorm.engine.physics2d.Rigid.isRigid
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algoventure.core.geometry2d.Direction

object Util {
    private fun getRigid(
            width: Int,
            height: Int,
            tileWidth: Int,
            tileHeight: Int,
            objectManager: ObjectManager
    ): BooleanArray {
        val isRigid = BooleanArray(width * height)
        objectManager.objects.forEach {
            if (it.isRigid) {

            }
        }
        return isRigid
    }
    /*fun findPath(
            objectManager: ObjectManager,
            objectId: Int,
            toX: Int,
            toY: Int
    ): List<Direction>? {
        val k = getWalkable()
        return null
    }*/
}
