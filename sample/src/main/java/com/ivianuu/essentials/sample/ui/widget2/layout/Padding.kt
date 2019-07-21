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

package com.ivianuu.essentials.sample.ui.widget2.layout

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updatePaddingRelative
import com.ivianuu.essentials.sample.ui.widget2.exp.AndroidContext
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.SingleChildViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget

open class Padding(
    child: Widget,
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    key: Any? = null
) : SingleChildViewGroupWidget<FrameLayout>(child, key) {

    constructor(
        child: Widget,
        padding: Int,
        key: Any? = null
    ) : this(child, padding, padding, padding, padding, key)

    override fun updateView(context: BuildContext, view: FrameLayout) {
        super.updateView(context, view)
        if (view.paddingLeft != left || view.paddingTop != top
            || view.paddingRight != right || view.paddingBottom != bottom
        ) {
            view.updatePaddingRelative(left, top, right, bottom)
        }
    }

    override fun createView(context: BuildContext) =
        FrameLayout(AndroidContext(context)).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

}