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

import com.aheidelbacher.algostorm.event.Event
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber
import com.aheidelbacher.algostorm.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.state.Object
import com.aheidelbacher.algostorm.systems.script.ScriptingSystem.RunScriptWithResult

class ActingSystem(
        private val objectGroup: ObjectGroup,
        private val publisher: Publisher
) : Subscriber {
    companion object {
        const val ACTOR_SCRIPT: String = "actorScript"
        const val SPEED: String = "speed"
        const val STAMINA: String = "stamina"

        val Object.isActor: Boolean
            get() = contains(ACTOR_SCRIPT) && contains(SPEED) &&
                    contains(STAMINA)

        val Object.actorScript: String
            get() = getString(ACTOR_SCRIPT)
                    ?: error("Object $id must contain $ACTOR_SCRIPT property!")

        val Object.speed: Int
            get() = getInt(SPEED)
                    ?: error("Object $id must contain $SPEED property!")

        val Object.stamina: Int
            get() = getInt(STAMINA)
                    ?: error("Object $id must contain $STAMINA property!")

        fun Object.addStamina(stamina: Int) {
            set(STAMINA, this.stamina + stamina)
        }
    }

    object NewAct : Event

    @Subscribe fun onActionCompleted(event: ActionCompleted) {
        objectGroup[event.objectId]?.let { it.addStamina(-event.usedStamina) }
        publisher.post(NewAct)
    }

    @Subscribe fun onNewAct(event: NewAct) {
        objectGroup.objectSet.filter { it.isActor }.maxBy {
            it.stamina
        }?.let { obj ->
            if (obj.stamina < 0) {
                publisher.post(NewTurn)
                objectGroup.objectSet.filter { it.isActor }.forEach {
                    it.addStamina(it.speed)
                }
            } else {
                publisher.publish(RunScriptWithResult(
                        obj.actorScript,
                        Action::class,
                        objectGroup,
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
