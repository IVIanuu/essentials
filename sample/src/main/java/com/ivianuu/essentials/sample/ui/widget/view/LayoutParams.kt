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

package com.ivianuu.essentials.sample.ui.widget.view

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget

fun BuildContext.Gravity(gravity: Int, child: Widget, key: Any? = null) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(FrameLayout.LayoutParams::gravity, LinearLayout.LayoutParams::gravity),
    applyViewProps = { view ->
        val lp = view.layoutParams
        when (lp) {
            is FrameLayout.LayoutParams -> lp.gravity = gravity
            is LinearLayout.LayoutParams -> lp.gravity = gravity
            else -> error("cannot apply gravity for $view parent is ${view.parent}")
        }

        view.layoutParams = lp
    }
)

fun BuildContext.Margin(margin: Int, child: Widget, key: Any? = null) =
    Padding(margin, margin, margin, margin, child, key)

fun BuildContext.Margin(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(View::setLayoutParams, ViewGroup.MarginLayoutParams::setMargins),
    applyViewProps = {
        it.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(left, top, right, bottom)
        }
    }
)

fun BuildContext.Weight(
    weight: Float,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(LinearLayout.LayoutParams::weight),
    applyViewProps = {
        it.updateLayoutParams<LinearLayout.LayoutParams> {
            this.weight = weight
        }
    }
)

