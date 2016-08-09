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

import com.aheidelbacher.algostorm.engine.script.ScriptEngine

import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.Function

import java.io.InputStream
import java.io.InputStreamReader

import kotlin.reflect.KClass

class JavascriptEngine : ScriptEngine {
    private companion object {
        inline fun <T> executeWithContext(block: Context.() -> T): T = try {
            ContextFactory.getGlobal().enterContext().apply {
                optimizationLevel = -1
            }.block()
        } finally {
            Context.exit()
        }
    }

    private val scope = executeWithContext { initStandardObjects() }

    override fun eval(script: InputStream) {
        executeWithContext {
            evaluateReader(scope, InputStreamReader(script), "script", 1, null)
        }
    }

    override fun <T : Any> invokeFunction(
            functionName: String,
            returnType: KClass<T>,
            vararg args: Any?
    ): T? = returnType.java.cast(Context.jsToJava(
            executeWithContext {
                (scope.get(functionName, scope) as Function).call(
                        this,
                        scope,
                        scope,
                        args
                )
            },
            returnType.java
    ))
}
