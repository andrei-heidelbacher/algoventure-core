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

package com.aheidelbacher.algoventure.core.hook

import com.aheidelbacher.algostorm.engine.script.RunScript
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.hook.Hooks.onMoved
import com.aheidelbacher.algoventure.core.move.Moved

class HookSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher
) : Subscriber {
    @Subscribe fun onMoved(event: Moved) {
        objectManager[event.objectId]?.let { movedObj ->
            objectManager.objects.forEach { obj ->
                obj.onMoved?.let { onMovedScript ->
                    publisher.post(RunScript(
                            onMovedScript,
                            objectManager,
                            obj,
                            movedObj,
                            event.direction
                    ))
                }
            }
        }
    }
}
