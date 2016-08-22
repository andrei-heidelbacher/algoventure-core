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

import com.aheidelbacher.algostorm.engine.script.RunScriptWithResult
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.act.Actor.actorScript
import com.aheidelbacher.algoventure.core.act.Actor.speed
import com.aheidelbacher.algoventure.core.act.Actor.stamina
import com.aheidelbacher.algoventure.core.act.Actor.isActor
import com.aheidelbacher.algoventure.core.act.Actor.addStamina

class ActingSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher
) : Subscriber {
    @Subscribe fun onActionCompleted(event: ActionCompleted) {
        objectManager[event.objectId]?.let {
            it.addStamina(-event.usedStamina)
        }
        publisher.post(NewAct)
    }

    @Subscribe fun onNewAct(event: NewAct) {
        objectManager.objects.filter { it.isActor }.maxBy {
            it.stamina
        }?.let { obj ->
            if (obj.stamina < 0) {
                publisher.post(NewTurn)
                objectManager.objects.filter { it.isActor }.forEach {
                    it.addStamina(obj.speed)
                }
            } else {
                publisher.publish(RunScriptWithResult(
                        obj.actorScript,
                        Action::class,
                        objectManager,
                        obj.id
                ) { action ->
                    if (action is Action? && action != null) {
                        require(action.objectId == obj.id) {
                            "Actor id ${action.objectId} should be ${obj.id}!"
                        }
                        publisher.post(action)
                    }
                })
            }
        }
    }
}
