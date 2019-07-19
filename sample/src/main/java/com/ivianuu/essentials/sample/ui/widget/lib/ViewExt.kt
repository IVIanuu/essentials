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

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.util.Properties
import com.ivianuu.kommon.core.view.getTagOrSet

val View.properties: Properties
    // todo unique tag
    get() = getTagOrSet(R.id.epoxy_saved_view_style) { Properties() }

fun ViewGroup.findContainerForWidget(widget: Widget<*>): ViewGroup {
    return if (widget.containerId != null) {
        findViewById(widget.containerId!!)
    } else {
        this as ViewGroup
    }
}

var View.boundWidget: Widget<*>?
    get() = properties.get<Widget<*>>("bound_widget")
    set(value) {
        properties.set("bound_widget", value)
    }

var View.laidOutWidget: Widget<*>?
    get() = properties.get<Widget<*>>("laid_out_widget")
    set(value) {
        properties.set("laid_out_widget", value)
    }

@JvmName("findViewByWidgetUntyped")
fun View.findViewByWidget(widget: Widget<*>): View? =
    findViewByWidget<View>(widget)

fun <T : View> View.findViewByWidget(widget: Widget<*>): T? {
    val id = widget.type.hashCode() + (widget.key?.hashCode() ?: 0)
    return findViewByWidgetIdTraversal<T>(id)
}

private fun <T : View> View.findViewByWidgetIdTraversal(widgetId: Int): T? {
    if (this.getWidgetId() == widgetId) return this as? T

    if (this is ViewGroup) {
        for (child in children.iterator()) {
            val result = child.findViewByWidgetIdTraversal<T>(widgetId)
            if (result != null) return result
        }
    }

    return null
}

fun View.getWidgetId(): Int? = properties.get("id")

fun View.setWidget(widget: Widget<*>) {
    properties.set("id", widget.type.hashCode() + (widget.key?.hashCode() ?: 0))
}