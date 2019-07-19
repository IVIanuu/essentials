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

/**
import android.view.ViewGroup

val RootViewAmbient = Ambient.of<ViewGroup>()

class Ambient<T>(private val key: String, private val defaultFactory: (() -> T)? = null) {
@Suppress("UNCHECKED_CAST")
internal val defaultValue by lazy {
val fn = defaultFactory
if (fn != null) fn()
else null as T
}

companion object {
inline fun <reified T> of(
key: String = T::class.java.simpleName,
noinline defaultFactory: (() -> T)? = null
) = Ambient(key, defaultFactory)
}

override fun hashCode() = key.hashCode()
override fun equals(other: Any?) = this === other
override fun toString(): String = "Ambient<$key>"

inner class Provider<T>(
val value: T,
child: Widget<*>
) : HeadlessWidget(child) {
val ambient = this@Ambient
}

}

fun <T> BuildContext.ambient(ambient: Ambient<T>): T {
var parent: BuildContext? = this
while (parent != null) {
if (parent is Ambient<*>.Provider<*> && parent.ambient == ambient) {
return parent.value as T
}

parent = parent.parent
}

return ambient.defaultValue
}*/