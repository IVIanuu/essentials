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

package com.ivianuu.essentials.sample.ui.widget.builder

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout

fun <V : View> ViewWidgetBuilder<V>.layoutWidth(width: Int) {
    updateLayoutParams {
        if (it.width != width) {
            it.width = width
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutHeight(height: Int) {
    updateLayoutParams {
        if (it.height != height) {
            it.height = height
            true
        } else {
            false
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutSize(size: Int) {
    layoutWidth(size)
    layoutHeight(size)
}

fun <V : View> ViewWidgetBuilder<V>.wrapContent() {
    layoutSize(WRAP_CONTENT)
}

fun <V : View> ViewWidgetBuilder<V>.matchParent() {
    layoutSize(MATCH_PARENT)
}

fun <V : View> ViewWidgetBuilder<V>.layoutMargin(margin: Int) {
    layoutMargin(margin, margin, margin, margin)
}

fun <V : View> ViewWidgetBuilder<V>.horizontalLayoutMargin(margin: Int) {
    layoutMargin(left = margin, right = margin)
}

fun <V : View> ViewWidgetBuilder<V>.verticalLayoutMargin(margin: Int) {
    layoutMargin(top = margin, bottom = margin)
}

fun <V : View> ViewWidgetBuilder<V>.layoutMargin(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0
) {
    updateLayoutParams {
        if (it is ViewGroup.MarginLayoutParams) {
            if (it.leftMargin != left
                || it.topMargin != top
                || it.rightMargin != right
                || it.bottomMargin != bottom
            ) {
                it.leftMargin = left
                it.topMargin = top
                it.rightMargin = right
                it.bottomMargin = bottom
                true
            } else {
                false
            }
        } else {
            error("cannot apply margin to $it")
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutGravity(gravity: Int) {
    updateLayoutParams { lp ->
        when (lp) {
            is FrameLayout.LayoutParams -> {
                if (lp.gravity != gravity) {
                    lp.gravity = gravity
                    true
                } else {
                    false
                }
            }
            is LinearLayout.LayoutParams -> {
                if (lp.gravity != gravity) {
                    lp.gravity = gravity
                    true
                } else {
                    false
                }
            }
            else -> error("cannot apply gravity to $lp")
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.layoutWeight(weight: Float) {
    updateLayoutParams {
        if (it is LinearLayout.LayoutParams) {
            if (it.weight != weight) {
                it.weight = weight
                true
            } else {
                false
            }
        } else {
            error("cannot apply weight to $it")
        }
    }
}