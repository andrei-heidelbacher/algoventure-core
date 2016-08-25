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

import com.aheidelbacher.algostorm.engine.script.ScriptingSystem.RunScriptWithResult
import com.aheidelbacher.algostorm.engine.state.Object
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

class ActingSystem(
        private val objectManager: ObjectManager,
        private val publisher: Publisher
) : Subscriber {
    companion object {
        const val ACTOR_SCRIPT: String = "actorScript"
        const val SPEED: String = "speed"
        const val STAMINA: String = "stamina"

        val Object.isActor: Boolean
            get() = contains(ACTOR_SCRIPT) && contains(SPEED) && contains(STAMINA)

        val Object.actorScript: String
            get() = get(ACTOR_SCRIPT) as String?
                    ?: error("Object $id must contain $ACTOR_SCRIPT property!")

        val Object.speed: Int
            get() = get(SPEED) as Int?
                    ?: error("Object $id must contain $SPEED property!")

        val Object.stamina: Int
            get() = get(STAMINA) as Int?
                    ?: error("Object $id must contain $STAMINA property!")

        fun Object.addStamina(stamina: Int) {
            set(STAMINA, this.stamina + stamina)
        }
    }

    object NewAct : Event

    @Subscribe fun onActionCompleted(event: ActionCompleted) {
        objectManager[event.objectId]?.let { it.addStamina(-event.usedStamina) }
        publisher.post(NewAct)
    }

    @Subscribe fun onNewAct(event: NewAct) {
        objectManager.objects.filter { it.isActor }.maxBy {
            it.stamina
        }?.let { obj ->
            if (obj.stamina < 0) {
                publisher.post(NewTurn)
                objectManager.objects.filter { it.isActor }.forEach {
                    it.addStamina(it.speed)
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
