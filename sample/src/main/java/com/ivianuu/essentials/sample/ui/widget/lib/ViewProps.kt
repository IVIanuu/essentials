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
import kotlin.reflect.KFunction2

fun <V : View, T> BuildContext.ViewPropsWidget(
    value: T,
    prop: KFunction2<V, T, Any?>,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    key = key,
    child = child,
    updateViewProps = { prop.invoke(it as V, value) }
)

fun BuildContext.ViewPropsWidget(
    key: Any? = null,
    updateViewProps: (View) -> Unit,
    child: BuildContext.() -> Unit
): Widget = object : ViewPropsWidget(child, key) {
    override fun updateViewProps(view: View) {
        updateViewProps.invoke(view)
    }
}

abstract class ViewPropsWidget(
    child: BuildContext.() -> Unit,
    key: Any? = null
) : ProxyWidget(key, child) {

    override fun createElement(): ViewPropsElement =
        ViewPropsElement(this)

    abstract fun updateViewProps(view: View)

}

open class ViewPropsElement(widget: ViewPropsWidget) : ProxyElement(widget) {

    fun updateViewProps(view: View) {
        widget<ViewPropsWidget>().updateViewProps(view)
    }

}