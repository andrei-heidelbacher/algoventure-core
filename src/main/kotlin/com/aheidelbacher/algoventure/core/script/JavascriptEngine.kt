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
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.Function
import org.mozilla.javascript.ScriptableObject

import java.io.Reader

import kotlin.reflect.KClass

class JavascriptEngine(scripts: List<Reader>) {
    companion object {
        inline fun <T> executeWithContext(block: Context.() -> T): T = try {
            ContextFactory.getGlobal().enterContext().apply {
                optimizationLevel = -1
            }.block()
        } finally {
            Context.exit()
        }
    }

    private val scope = executeWithContext { initStandardObjects() }

    init {
        executeWithContext {
            scripts.forEach {
                evaluateReader(scope, it, "loaded_script", 1, null)
            }
        }
    }

    fun put(key: String, value: Any?) {
        ScriptableObject.putProperty(scope, key, Context.javaToJS(value, scope))
    }

    fun <T : Any> runScript(
            scriptUri: String,
            resultType: KClass<T>,
            vararg args: Any?
    ): T? = resultType.java.cast(Context.jsToJava(
            executeWithContext {
                (scope.get(scriptUri, scope) as Function).call(
                        this,
                        scope,
                        scope,
                        args
                )
            },
            resultType.java
    ))

    inline fun <reified T : Any> runScript(
            scriptUri: String,
            vararg args: Any?
    ): T? = runScript(scriptUri, T::class, *args)
}
