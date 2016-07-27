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

package com.aheidelbacher.algoventure.core.script

import com.aheidelbacher.algostorm.script.AbstractScriptingSystem

class ScriptingSystem(
        private val scriptingEngine: JavascriptEngine
) : AbstractScriptingSystem() {
    override fun put(key: String, value: Any?) {
        scriptingEngine.put(key, value)
    }

    override fun runScript(scriptUri: String, vararg args: Any?): Any? =
            scriptingEngine.runScript(scriptUri, *args)
}
