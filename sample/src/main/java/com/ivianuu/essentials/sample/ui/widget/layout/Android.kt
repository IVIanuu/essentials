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

package com.ivianuu.essentials.sample.ui.widget.layout

import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget

fun BuildContext.FrameLayout(children: List<Widget>, key: Any? = null) =
    ViewGroupWidget<FrameLayout>(children = children, key = key)

fun BuildContext.LinearLayout(
    orientation: Int = LinearLayout.VERTICAL,
    gravity: Int = if (orientation == LinearLayout.VERTICAL) {
        Gravity.TOP or Gravity.CENTER_HORIZONTAL
    } else {
        Gravity.START or Gravity.CENTER_HORIZONTAL
    },
    children: List<Widget>,
    key: Any? = null
) = ViewGroupWidget<LinearLayout>(children, key) { view ->
    view.orientation = orientation
    view.gravity = gravity
}

fun BuildContext.ScrollView(child: Widget, key: Any? = null) =
    ViewGroupWidget<ScrollView>(listOf(child), key)