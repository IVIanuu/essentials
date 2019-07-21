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

import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import com.ivianuu.essentials.sample.ui.widget2.exp.AndroidContext
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.SingleChildViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget

class Container(
    child: Widget,
    val width: Int = WRAP_CONTENT,
    val height: Int = WRAP_CONTENT,
    val gravity: Int = Gravity.START or Gravity.TOP,
    padding: Int = 0,
    val paddingLeft: Int = padding,
    val paddingTop: Int = padding,
    val paddingRight: Int = padding,
    val paddingBottom: Int = padding,
    key: Any? = null
) : SingleChildViewGroupWidget<LinearLayout>(child, key) {

    override fun updateView(context: BuildContext, view: LinearLayout) {
        super.updateView(context, view)
        if (view.layoutParams.width != width || view.layoutParams.height != height) {
            view.updateLayoutParams {
                width = this@Container.width
                height = this@Container.height
            }
        }

        view.gravity = gravity

        if (view.paddingLeft != paddingLeft || view.paddingTop != paddingTop
            || view.paddingRight != paddingRight || view.paddingBottom != paddingBottom
        ) {
            view.updatePaddingRelative(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }

    override fun createView(context: BuildContext) =
        LinearLayout(AndroidContext(context)).apply {
            layoutParams = ViewGroup.LayoutParams(this@Container.width, this@Container.height)
        }
}