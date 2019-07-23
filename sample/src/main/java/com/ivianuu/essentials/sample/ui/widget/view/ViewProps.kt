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
import com.ivianuu.kommon.core.content.drawableAttr

fun Alpha(alpha: Float, key: Any? = null, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = alpha, prop = View::setAlpha, child = child, key = key)

fun Background(
    color: Int? = null,
    drawable: Drawable? = null,
    resource: Int? = null,
    attr: Int? = null,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
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
    },
    child = child
)

fun Enabled(enabled: Boolean, key: Any? = null, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = enabled, prop = View::setEnabled, key = key, child = child)

fun Elevation(elevation: Float, key: Any? = null, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = elevation, prop = View::setAlpha, key = key, child = child)

fun Focusable(focusable: Boolean, key: Any? = null, child: BuildContext.() -> Unit) =
    ViewPropsWidget<View, Boolean>(
        value = focusable,
        prop = View::setFocusable,
        key = key,
        child = child
    )

fun Id(id: Int, key: Any? = null, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = id, prop = View::setId, key = key, child = child)

fun Padding(padding: Int, key: Any? = null, child: BuildContext.() -> Unit) =
    Padding(padding, padding, padding, padding, key, child)

fun Padding(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    key = key,
    props = listOf(View::setPadding),
    applyViewProps = { it.updatePaddingRelative(left, top, right, bottom) },
    child = child
)

fun Pivot(
    x: Float,
    y: Float,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    key = key,
    props = listOf(View::setPivotX, View::setPivotY),
    applyViewProps = { it.pivotX = x; it.pivotY = y },
    child = child
)

fun Rotation(rotation: Float, key: Any? = null, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = rotation, prop = View::setRotation, key = key, child = child)

fun Scale(
    x: Float,
    y: Float,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    key = key,
    props = listOf(View::setScaleX, View::setScaleY),
    applyViewProps = { it.scaleX = x; it.scaleY = y },
    child = child
)

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
) = ViewPropsWidget(
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
    },
    child = child
)

fun Translation(
    x: Float,
    y: Float,
    z: Float,
    key: Any? = null,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    key = key,
    props = listOf(View::setTranslationX, View::setTranslationY, View::setTranslationZ),
    applyViewProps = { it.translationX = x; it.translationY = y; it.translationZ = z },
    child = child
)

fun Visible(key: Any? = null, child: BuildContext.() -> Unit) = Visibility(View.VISIBLE, key, child)
fun Invisible(key: Any? = null, child: BuildContext.() -> Unit) =
    Visibility(View.INVISIBLE, key, child)

fun Gone(key: Any? = null, child: BuildContext.() -> Unit) = Visibility(View.GONE, key, child)
fun Visibility(visibility: Int, key: Any? = null, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = visibility, prop = View::setVisibility, child = child, key = key)