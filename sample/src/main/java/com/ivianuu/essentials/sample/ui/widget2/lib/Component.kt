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

package com.ivianuu.essentials.sample.ui.widget2.lib

import android.content.Context
import com.github.ajalt.timberkt.d

abstract class ComponentElement(widget: Widget) : Element(widget) {

    var child: Element? = null
        private set

    abstract fun build(): Widget

    override fun mount(context: Context, parent: Element?, slot: Int?) {
        super.mount(context, parent, slot)
        firstBuild(context)
    }

    override fun attachView() {
        super.attachView()
        child?.attachView()
    }

    override fun detachView() {
        super.detachView()
        child?.detachView()
    }

    override fun unmount() {
        child?.unmount()
        child = null
        super.unmount()
    }

    override fun update(context: Context, newWidget: Widget) {
        super.update(context, newWidget)
        d { "component ${javaClass.simpleName} update $newWidget" }
        performRebuild(context)
    }

    protected open fun firstBuild(context: Context) {
        rebuild(context)
    }

    override fun performRebuild(context: Context) {
        d { "${javaClass.simpleName} perform rebuild" }
        val built = build()
        isDirty = false
        child = updateChild(context, child, built, slot)
    }

}