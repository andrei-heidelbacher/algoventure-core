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

package com.aheidelbacher.algoventure.core.act

import com.aheidelbacher.algostorm.engine.script.ScriptEngine.Companion.invokeFunction
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.act.Actor.Companion.actor
import com.aheidelbacher.algoventure.core.script.JavascriptEngine

class ActingSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher,
        private val scriptEngine: JavascriptEngine
) : Subscriber {
    @Subscribe fun handleAct(event: NewAct) {
        objectManager[event.actorId]?.let { obj ->
            obj.actor?.scriptFunctionName?.let { functionName ->
                scriptEngine.invokeFunction<Action>(
                        functionName,
                        objectManager,
                        obj.id
                )?.let { action ->
                    require(action.actorId == obj.id) {
                        "Invalid actor id!" +
                                "Expected ${obj.id}, received ${action.actorId}"
                    }
                    println("Recorded action $action from ${obj.id}!")
                    publisher.post(action)
                }
            }
        }
    }
}
