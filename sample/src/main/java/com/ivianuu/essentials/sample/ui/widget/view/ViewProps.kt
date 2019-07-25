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

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.updatePaddingRelative
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewPropsWidget
import com.ivianuu.kommon.core.view.drawableAttr

fun BuildContext.Alpha(alpha: Float, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = alpha, prop = View::setAlpha, child = child)

fun BuildContext.Background(
    color: Int? = null,
    drawable: Drawable? = null,
    resource: Int? = null,
    attr: Int? = null,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View::setBackgroundColor, View::setBackground, View::setBackgroundResource),
    updateViewProps = { view ->
        when {
            color != null -> view.setBackgroundColor(color)
            drawable != null -> view.background = drawable
            resource != null -> view.setBackgroundResource(resource)
            attr != null -> view.background = view.drawableAttr(attr)
            else -> view.background = null
        }
    },
    child = child
)

fun BuildContext.Enabled(enabled: Boolean, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = enabled, prop = View::setEnabled, child = child)

fun BuildContext.Elevation(elevation: Float, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = elevation, prop = View::setAlpha, child = child)

fun BuildContext.Focusable(focusable: Boolean, child: BuildContext.() -> Unit) =
    ViewPropsWidget<View, Boolean>(
        value = focusable,
        prop = View::setFocusable,
        child = child
    )

fun BuildContext.Id(id: Int, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = id, prop = View::setId, child = child)

fun BuildContext.MinSize(
    minWidth: Int = 0,
    minHeight: Int = 0,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View::setMinimumWidth, View::setMinimumHeight),
    updateViewProps = {
        it.minimumWidth = minWidth
        it.minimumHeight = minHeight
    },
    child = child
)

fun BuildContext.Padding(padding: Int, child: BuildContext.() -> Unit) =
    Padding(padding, padding, padding, padding, child)

fun BuildContext.Padding(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View::setPadding),
    updateViewProps = { it.updatePaddingRelative(left, top, right, bottom) },
    child = child
)

fun BuildContext.Pivot(
    x: Float,
    y: Float,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View::setPivotX, View::setPivotY),
    updateViewProps = { it.pivotX = x; it.pivotY = y },
    child = child
)

fun BuildContext.Rotation(rotation: Float, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = rotation, prop = View::setRotation, child = child)

fun BuildContext.Scale(
    x: Float,
    y: Float,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View::setScaleX, View::setScaleY),
    updateViewProps = { it.scaleX = x; it.scaleY = y },
    child = child
)

fun BuildContext.Translation(
    x: Float,
    y: Float,
    z: Float,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    props = listOf(View::setTranslationX, View::setTranslationY, View::setTranslationZ),
    updateViewProps = {
        it.translationX = x;
        it.translationY = y;
        it.translationZ = z
    },
    child = child
)

fun BuildContext.Visible(child: BuildContext.() -> Unit) = Visibility(View.VISIBLE, child)
fun BuildContext.Invisible(child: BuildContext.() -> Unit) =
    Visibility(View.INVISIBLE, child)

fun BuildContext.Gone(child: BuildContext.() -> Unit) = Visibility(View.GONE, child)
fun BuildContext.Visibility(visibility: Int, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = visibility, prop = View::setVisibility, child = child)