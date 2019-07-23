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

interface BuildContext {

    val widget: Widget
    val owner: BuildOwner?

    fun add(child: Widget)
    operator fun Widget.unaryPlus() = add(this)

    fun <T> getAmbient(key: Ambient<T>): T?

    fun <T> cache(calculation: () -> T): T

    fun <T> cache(vararg inputs: Any?, calculation: () -> T): T

    operator fun <T> Ambient<T>.unaryPlus() = invoke(this@BuildContext)

    operator fun <T> Effect<T>.unaryPlus() = invoke(this@BuildContext)

}