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

package com.ivianuu.essentials.sample.ui.widget.lib

import android.content.Context

inline fun <reified T> ambientOf(
    noinline defaultValue: (() -> T)? = null
): Ambient<T> = ambientOf(T::class.java.name to sourceLocation(), defaultValue)

fun <T> ambientOf(key: Any, defaultValue: (() -> T)? = null): Ambient<T> =
    Ambient(key, defaultValue)

class Ambient<T> @PublishedApi internal constructor(
    val key: Any,
    private val defaultValue: (() -> T)? = null
) {

    operator fun invoke(context: BuildContext): T {
        return context.getAmbient(this)
            ?: defaultValue?.invoke() as T
    }

    inner class Provider(
        val value: T,
        child: BuildContext.() -> Unit
    ) : ProxyWidget(child = child) {

        val ambientKey: Any get() = this@Ambient.key

        override fun createElement(): ProxyElement = Element(this)

        private inner class Element(widget: ProxyWidget) : ProxyElement(widget) {
            override fun notifyClients(oldWidget: Widget) {
                super.notifyClients(oldWidget)
                dependents?.forEach { it.markNeedsBuild() }
            }
        }

    }
}

val ContextAmbient = ambientOf<Context>()