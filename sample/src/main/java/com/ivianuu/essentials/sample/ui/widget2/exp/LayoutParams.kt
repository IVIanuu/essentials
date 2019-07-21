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

package com.ivianuu.essentials.sample.ui.widget2.exp

import android.view.View
import androidx.core.view.updateLayoutParams
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.ui.widget2.lib.Element
import com.ivianuu.essentials.sample.ui.widget2.lib.ProxyElement
import com.ivianuu.essentials.sample.ui.widget2.lib.ProxyWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewElement
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget

open class LayoutParamsWidget(
    child: Widget,
    val height: Int? = null,
    val width: Int? = null,
    key: Any? = null
) : ProxyWidget(child, key) {

    override fun createElement(): LayoutParamsElement = LayoutParamsElement(this)

    fun applyLayoutParams(view: View) {
        d { "apply layout params $view" }
        if (height != null || width != null) {
            view.updateLayoutParams {
                this@LayoutParamsWidget.width?.let { width = it }
                this@LayoutParamsWidget.height?.let { height = it }
            }
        }
    }

}

open class LayoutParamsElement(widget: LayoutParamsWidget) : ProxyElement(widget) {

    fun applyLayoutParams(widget: LayoutParamsWidget) {
        d { "${javaClass.simpleName} apply layout params $widget" }

        lateinit var applyLayoutParamsToChild: (Element) -> Unit

        applyLayoutParamsToChild = { child ->
            d { "${javaClass.simpleName} apply layout params to child $child" }
            if (child is ViewElement<*>) {
                child.updateLayoutParams(widget)
            } else {
                child.onEachChild(applyLayoutParamsToChild)
            }
        }

        onEachChild(applyLayoutParamsToChild)
    }

    override fun notifyClients(oldWidget: Widget) {
        applyLayoutParams(widget())
    }

}