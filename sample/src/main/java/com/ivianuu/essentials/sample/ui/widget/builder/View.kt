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

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.updatePaddingRelative
import com.ivianuu.kommon.core.view.drawableAttr

fun <V : View> ViewWidgetBuilder<V>.background(
    color: Int? = null,
    drawable: Drawable? = null,
    resource: Int? = null,
    attr: Int? = null
) {
    updateView {
        when {
            color != null -> it.setBackgroundColor(color)
            drawable != null -> it.background = drawable
            resource != null -> it.setBackgroundResource(resource)
            attr != null -> it.background = it.drawableAttr(attr)
            else -> it.background = null
        }
    }
}

fun <V : View> ViewWidgetBuilder<V>.elevation(elevation: Float) {
    updateView { it.elevation = elevation }
}

fun <V : View> ViewWidgetBuilder<V>.id(id: Int) {
    updateView { it.id = id }
}

fun <V : View> ViewWidgetBuilder<V>.onClick(onClick: () -> Unit) {
    updateView { it.setOnClickListener { onClick() } }
}

fun <V : View> ViewWidgetBuilder<V>.onLongClick(onLongClick: () -> Unit) {
    updateView { it.setOnClickListener { onLongClick() } }
}

fun <V : View> ViewWidgetBuilder<V>.padding(padding: Int) {
    padding(padding, padding, padding, padding)
}

fun <V : View> ViewWidgetBuilder<V>.horizontalpadding(padding: Int) {
    padding(left = padding, right = padding)
}

fun <V : View> ViewWidgetBuilder<V>.verticalpadding(padding: Int) {
    padding(top = padding, bottom = padding)
}

fun <V : View> ViewWidgetBuilder<V>.padding(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0
) {
    updateView {
        it.updatePaddingRelative(left, top, right, bottom)
    }
}