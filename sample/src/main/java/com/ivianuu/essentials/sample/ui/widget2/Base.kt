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

package com.ivianuu.essentials.sample.ui.widget2

import android.content.Context
import kotlin.reflect.KClass

abstract class Widget(open val key: Any? = null) {

    abstract fun createElement(): Element

    fun canUpdate(other: Widget): Boolean =
        this::class == other::class && this.key == other.key

}

abstract class Element : BuildContext {

    abstract val widget: Widget

    var context: Context? = null
        private set
    var parent: Element? = null
        private set
    var slot: Int? = null
        private set

    var inheritedWidgets: MutableMap<KClass<out InheritedWidget>, InheritedElement>? = null

    override fun <T : InheritedWidget> ancestorInheritedElementForWidgetOfExactType(type: KClass<T>): T? =
        inheritedWidgets?.get(type)?.widget as? T

    open fun mount(context: Context, parent: Element?, slot: Int?) {
        this.context = context
        this.parent = parent
        this.slot = slot
        updateInheritance()
    }

    open fun unmount() {
        context = null
        parent = null
        slot = null
        inheritedWidgets = null
    }

    open fun attachView() {
    }

    open fun detachView() {
    }

    protected open fun updateInheritance() {
        inheritedWidgets = parent?.inheritedWidgets
    }
}