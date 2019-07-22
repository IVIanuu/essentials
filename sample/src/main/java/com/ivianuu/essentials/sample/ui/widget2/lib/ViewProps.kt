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

import android.view.View
import com.github.ajalt.timberkt.d

fun ViewPropsWidget(
    child: Widget,
    key: Any? = null,
    applyViewProps: (BuildContext, View) -> Unit
) = object : ViewPropsWidget(child, key) {
    override fun applyViewProps(context: BuildContext, view: View) {
        applyViewProps(context, view)
    }
}

abstract class ViewPropsWidget(
    child: Widget,
    key: Any? = null
) : ProxyWidget(child, key) {

    override fun createElement(): ViewPropsElement =
        ViewPropsElement(this)

    abstract fun applyViewProps(context: BuildContext, view: View)

}

open class ViewPropsElement(widget: ViewPropsWidget) : ProxyElement(widget) {

    fun applyViewProps(widget: ViewPropsWidget) {
        d { "${javaClass.simpleName} apply view props $widget" }

        lateinit var applyLayoutParamsToChild: (Element) -> Unit

        applyLayoutParamsToChild = { child ->
            d { "${javaClass.simpleName} apply view props to child $child" }
            if (child is ViewElement<*>) {
                child.updateViewProps(widget)
            } else {
                child.onEachChild(applyLayoutParamsToChild)
            }
        }

        onEachChild(applyLayoutParamsToChild)
    }

    override fun notifyClients(oldWidget: Widget) {
        applyViewProps(widget())
    }

}