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

import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject

import java.io.FileReader

class JavascriptEngine {
    private companion object {
        const val ERROR_LOG_FILE: String = "script_errors.log"
    }

    private val context = Context.enter().apply { optimizationLevel = -1 }
    private val scope = context.initStandardObjects()

    fun put(key: String, value: Any?) {
        ScriptableObject.putProperty(scope, key, Context.javaToJS(value, scope))
    }

    fun runScript(scriptUri: String, vararg args: Any?): Any? =
            context.evaluateReader(
                    scope,
                    FileReader(scriptUri),
                    ERROR_LOG_FILE,
                    1,
                    null
            )

    fun shutdown() {
        Context.exit()
    }
}
