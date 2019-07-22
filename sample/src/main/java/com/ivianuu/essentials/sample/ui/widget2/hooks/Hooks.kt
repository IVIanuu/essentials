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

import android.annotation.SuppressLint
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.Element
import com.ivianuu.essentials.sample.ui.widget2.lib.State
import com.ivianuu.essentials.sample.ui.widget2.lib.StatefulElement
import com.ivianuu.essentials.sample.ui.widget2.lib.StatefulWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget

abstract class Hook<R>(val keys: List<Any?>? = null) {

    abstract fun createState(): HookState<R, Hook<R>>

    companion object {
        fun <R> use(hook: Hook<R>): R = HookElement.currentContext!!.use(hook)
    }
}

abstract class HookState<R, T : Hook<R>> {

    protected val context: BuildContext? get() = element?.context
    var element: State? = null

    var hook: T? = null

    open fun initHook() {
    }

    open fun dispose() {
    }

    open fun didBuild() {

    }

    abstract fun build(context: BuildContext): R

    open fun didUpdateHook(oldHook: T?) {
    }

    protected open fun setState(block: () -> Unit) {
        block()
        element!!.setState(block)
    }

}

class HookElement(
    widget: HookWidget
) : StatefulElement(widget) {

    private val hooks = mutableListOf<HookState<*, *>>()
    private var hookIndex = 0

    override fun build(): Widget {
        hookIndex = 0
        currentContext = this
        val result = super.build()
        currentContext = null
        return result
    }

    override fun updateChild(child: Element?, newWidget: Widget?, newSlot: Int?): Element? {
        hooks.reversed().forEach { it.didBuild() }
        return super.updateChild(child, newWidget, newSlot)
    }

    override fun unmount() {
        super.unmount()
        hooks.reversed().forEach { it.dispose() }
    }

    internal fun <R> use(hook: Hook<R>): R {
        val hookState: HookState<R, Hook<R>>

        if (hooks.isEmpty()) {
            hookState = createHookState(hook)
            hooks.add(hookState)
        } else {
            val prevState = hooks.last()
            when {
                prevState.hook == hook -> hookState = prevState as HookState<R, Hook<R>>
                prevState.hook!!.keys == hook.keys -> {
                    hookState = prevState as HookState<R, Hook<R>>
                    val previousHook = hookState.hook
                    hookState.hook = hook
                    hookState.didUpdateHook(previousHook)
                }
                else -> {
                    hooks.removeAt(hookIndex).dispose()
                    hookState = createHookState(hook)
                    hooks.add(hookIndex, hookState)
                }
            }
        }

        hookIndex++

        return hookState.build(this)
    }

    private fun <R> createHookState(hook: Hook<R>): HookState<R, Hook<R>> {
        val state = hook.createState()
        state.element = this.state
        state.hook = hook
        state.initHook()
        return state
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        internal var currentContext: HookElement? = null
    }

}

abstract class HookWidget(
    key: Any? = null
) : StatefulWidget(key) {

    override fun createElement(): StatefulElement =
        HookElement(this)

    override fun createState(): State =
        HookWidgetState()

    abstract fun build(context: BuildContext): Widget

}

private class HookWidgetState : State() {
    override fun build(context: BuildContext) = widget<HookWidget>().build(context)
}