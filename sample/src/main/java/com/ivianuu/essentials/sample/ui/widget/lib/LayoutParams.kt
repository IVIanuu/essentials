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

import android.view.ViewGroup

fun LayoutParamsWidget(
    key: Any? = null,
    props: List<Any?>,
    updateLayoutParams: (ViewGroup.LayoutParams) -> Boolean,
    child: BuildContext.() -> Unit
): Widget = object : LayoutParamsWidget(child, joinKey(props, key)) {
    override fun updateLayoutParams(layoutParams: ViewGroup.LayoutParams): Boolean =
        updateLayoutParams.invoke(layoutParams)
}

abstract class LayoutParamsWidget(
    child: BuildContext.() -> Unit,
    key: Any? = null
) : ProxyWidget(key, child) {

    override fun createElement(): LayoutParamsElement =
        LayoutParamsElement(this)

    abstract fun updateLayoutParams(layoutParams: ViewGroup.LayoutParams): Boolean

}

open class LayoutParamsElement(widget: LayoutParamsWidget) : ProxyElement(widget) {
    fun updateLayoutParams(layoutParams: ViewGroup.LayoutParams): Boolean =
        widget<LayoutParamsWidget>().updateLayoutParams(layoutParams)
}