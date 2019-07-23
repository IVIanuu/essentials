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
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import com.ivianuu.essentials.sample.ui.widget.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.content.drawableAttr

fun BuildContext.Alpha(alpha: Float, child: Widget, key: Any? = null) =
    ViewPropsWidget(value = alpha, prop = View::setAlpha, child = child, key = key)

fun BuildContext.Background(
    color: Int? = null,
    drawable: Drawable? = null,
    resource: Int? = null,
    attr: Int? = null,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(View::setBackgroundColor, View::setBackground, View::setBackgroundResource),
    applyViewProps = { view ->
        val context = +AndroidContextAmbient
        when {
            color != null -> view.setBackgroundColor(color)
            drawable != null -> view.background = drawable
            resource != null -> view.setBackgroundResource(resource)
            attr != null -> view.background = context.drawableAttr(attr)
            else -> view.background = null
        }
    }
)

fun BuildContext.Enabled(enabled: Boolean, child: Widget, key: Any? = null) =
    ViewPropsWidget(value = enabled, prop = View::setEnabled, child = child, key = key)

fun BuildContext.Elevation(elevation: Float, child: Widget, key: Any? = null) =
    ViewPropsWidget(value = elevation, prop = View::setAlpha, child = child, key = key)

fun BuildContext.Focusable(focusable: Boolean, child: Widget, key: Any? = null) =
    ViewPropsWidget<View, Boolean>(
        value = focusable,
        prop = View::setFocusable,
        child = child,
        key = key
    )

fun BuildContext.Id(id: Int, child: Widget, key: Any? = null) =
    ViewPropsWidget(value = id, prop = View::setId, child = child, key = key)

fun BuildContext.Padding(padding: Int, child: Widget, key: Any? = null) =
    Padding(padding, padding, padding, padding, child, key)

fun BuildContext.Padding(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(View::setPadding),
    applyViewProps = { it.updatePaddingRelative(left, top, right, bottom) }
)

fun BuildContext.Pivot(
    x: Float,
    y: Float,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(View::setPivotX, View::setPivotY),
    applyViewProps = { it.pivotX = x; it.pivotY = y }
)

fun BuildContext.Rotation(rotation: Float, child: Widget, key: Any? = null) =
    ViewPropsWidget(value = rotation, prop = View::setRotation, child = child, key = key)

fun BuildContext.Scale(
    x: Float,
    y: Float,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(View::setScaleX, View::setScaleY),
    applyViewProps = { it.scaleX = x; it.scaleY = y }
)

fun BuildContext.MatchParent(child: Widget, key: Any? = null) =
    Size(size = ViewGroup.LayoutParams.MATCH_PARENT, child = child, key = key)

fun BuildContext.WrapContent(child: Widget, key: Any? = null) =
    Size(size = ViewGroup.LayoutParams.WRAP_CONTENT, child = child, key = key)

fun BuildContext.Size(size: Int, child: Widget, key: Any? = null) =
    Size(width = size, height = size, child = child, key = key)

fun BuildContext.Size(
    width: Int,
    height: Int,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(
        View::setLayoutParams,
        ViewGroup.LayoutParams::width,
        ViewGroup.LayoutParams::height
    ),
    applyViewProps = {
        it.updateLayoutParams {
            this.width = width
            this.height = height
        }
    }
)

fun BuildContext.Translation(
    x: Float,
    y: Float,
    z: Float,
    child: Widget,
    key: Any? = null
) = ViewPropsWidget(
    child = child,
    key = key,
    props = listOf(View::setTranslationX, View::setTranslationY, View::setTranslationZ),
    applyViewProps = { it.translationX = x; it.translationY = y; it.translationZ = z }
)

fun BuildContext.Visible(child: Widget, key: Any? = null) = Visibility(View.VISIBLE, child, key)
fun BuildContext.Invisible(child: Widget, key: Any? = null) = Visibility(View.INVISIBLE, child, key)
fun BuildContext.Gone(child: Widget, key: Any? = null) = Visibility(View.GONE, child, key)
fun BuildContext.Visibility(visibility: Int, child: Widget, key: Any? = null) =
    ViewPropsWidget(value = visibility, prop = View::setVisibility, child = child, key = key)