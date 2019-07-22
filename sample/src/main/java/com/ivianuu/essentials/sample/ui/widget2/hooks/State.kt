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

package com.ivianuu.essentials.sample.ui.widget2.hooks

import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.util.cast
import kotlin.reflect.KProperty

class State<T> internal constructor(value: T, private val onChange: (T) -> Unit) {

    var value: T = value
        set(value) {
            field = value
            onChange(value)
        }

    operator fun component1(): T = value

    operator fun component2(): (T) -> Unit = { value = it }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }
}

fun <T> HookWidget.useState(init: () -> T): State<T> {
    return Hook.use(StateHook(init))
}

private class StateHook<T>(val init: () -> T) : Hook<State<T>>() {
    override fun createState() = StateHookState<T>()
}

private class StateHookState<T> : HookState<State<T>, Hook<State<T>>>() {

    private lateinit var state: State<T>

    override fun initHook() {
        super.initHook()
        state = State(hook!!.cast<StateHook<T>>().init()) {
            setState {
            }
        }
    }

    override fun build(context: BuildContext): State<T> = state

}