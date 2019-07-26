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

abstract class BuildContext {

    abstract val widget: Widget
    abstract val owner: BuildOwner?

    abstract fun add(id: Any, child: Widget)
    inline operator fun Widget.unaryPlus() = add(sourceLocation(), this)

    abstract fun <T> cache(id: Any, calculation: () -> T): T
    inline fun <T> cache(noinline calculation: () -> T): T =
        cache(id = sourceLocation(), calculation = calculation)

    abstract fun <T> cache(id: Any, vararg inputs: Any?, calculation: () -> T): T
    inline fun <T> cache(vararg inputs: Any?, noinline calculation: () -> T): T = cache(
        id = sourceLocation(), inputs = *inputs, calculation = calculation
    )

    operator fun <T> Effect<T>.unaryPlus() = invoke(this@BuildContext)

}