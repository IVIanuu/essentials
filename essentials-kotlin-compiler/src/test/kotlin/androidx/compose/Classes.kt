/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Composable

annotation class Pivotal

val composer: ViewComposition get() = ViewComposition()

class ViewComposition {

    @Suppress("NOTHING_TO_INLINE")
    inline fun joinKey(left: Any, right: Any?): Any = Unit

    @Suppress("PLUGIN_WARNING")
    inline fun call(
        key: Any,
        /*crossinline*/
        invalid: ViewValidator.() -> Boolean,
        block: @Composable() () -> Unit
    ) {
    }

    @Suppress("PLUGIN_WARNING")
    inline fun <T> expr(
        key: Any,
        block: () -> T
    ): T = block()

    inline fun <T> call(
        key: Any,
        /*crossinline*/
        ctor: () -> T,
        /*crossinline*/
        invalid: ViewValidator.(f: T) -> Boolean,
        block: (f: T) -> Unit
    ) {
    }

    fun startRestartGroup(key: Any) {
    }

    fun endRestartGroup(): ScopeUpdateScope? = null
}

class ViewValidator {
    fun <T> changed(value: T): Boolean = true
}

interface ScopeUpdateScope {
    fun updateScope(block: () -> Unit)
}
