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

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.LayoutParamsWidget

fun MatchParent(key: Any? = null, child: BuildContext.() -> Unit) =
    Size(size = ViewGroup.LayoutParams.MATCH_PARENT, key = key, child = child)

fun WrapContent(key: Any? = null, child: BuildContext.() -> Unit) =
    Size(size = ViewGroup.LayoutParams.WRAP_CONTENT, key = key, child = child)

fun Size(size: Int, key: Any? = null, child: BuildContext.() -> Unit) =
    Size(width = size, height = size, key = key, child = child)

fun Size(
    width: Int,
    height: Int,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = LayoutParamsWidget(
    key = key,
    props = listOf(
        ViewGroup.LayoutParams::width,
        ViewGroup.LayoutParams::height
    ),
    updateLayoutParams = {
        it.width = width
        it.height = height
    },
    child = child
)

fun Gravity(gravity: Int, key: Any? = null, child: BuildContext.() -> Unit) = LayoutParamsWidget(
    key = key,
    props = listOf(FrameLayout.LayoutParams::gravity, LinearLayout.LayoutParams::gravity),
    updateLayoutParams = { lp ->
        when (lp) {
            is FrameLayout.LayoutParams -> lp.gravity = gravity
            is LinearLayout.LayoutParams -> lp.gravity = gravity
            else -> error("cannot apply gravity to $lp")
        }
    },
    child = child
)

fun Margin(margin: Int, key: Any? = null, child: BuildContext.() -> Unit) =
    Margin(margin, margin, margin, margin, key, child)

fun Margin(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = LayoutParamsWidget(
    key = key,
    props = listOf(ViewGroup.MarginLayoutParams::setMargins),
    updateLayoutParams = {
        if (it is ViewGroup.MarginLayoutParams) {
            it.setMargins(left, top, right, bottom)
        } else {
            error("cannot apply margin to $it")
        }
    },
    child = child
)

fun Weight(
    weight: Float,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = LayoutParamsWidget(
    key = key,
    props = listOf(LinearLayout.LayoutParams::weight),
    updateLayoutParams = {
        if (it is LinearLayout.LayoutParams) {
            it.weight = weight
        } else {
            error("cannot apply weight to $it")
        }
    },
    child = child
)