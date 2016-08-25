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

package com.aheidelbacher.algoventure.core.log

import com.aheidelbacher.algostorm.engine.Update
import com.aheidelbacher.algostorm.engine.graphics2d.RenderingSystem.Render
import com.aheidelbacher.algostorm.engine.graphics2d.camera.CameraSystem.UpdateCamera
import com.aheidelbacher.algostorm.engine.input.AbstractInputSystem.HandleInput
import com.aheidelbacher.algostorm.engine.log.Logger
import com.aheidelbacher.algostorm.engine.script.ScriptingSystem.RunScriptWithResult
import com.aheidelbacher.algostorm.event.Event

import com.aheidelbacher.algoventure.core.act.ActingSystem.NewAct
import com.aheidelbacher.algoventure.core.damage.HealthBarSystem.UpdateHealthBars
import com.aheidelbacher.algoventure.core.graphics2d.RenderOrderSystem.SortObjects

class EventSystemLogger : Logger {
    override fun log(event: Event) {
        when (event) {
            is HandleInput -> {}
            is Update -> {}
            is UpdateCamera -> {}
            is SortObjects -> {}
            is Render -> {}
            is NewAct -> {}
            is UpdateHealthBars -> {}
            is RunScriptWithResult -> {
                if (event.functionName != "getPlayerInput") {
                    println(event)
                }
            }
            else -> println(event)
        }
    }
}
