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
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.content.drawableAttr

open class Ripple(
    val unbounded: Boolean = false,
    val child: Widget,
    key: Any? = null
) : StatelessWidget(key) {
    override fun build(context: BuildContext): Widget {
        return Background(
            attr = if (unbounded) R.attr.selectableItemBackgroundBorderless
            else R.attr.selectableItemBackground,
            child = child
        )
    }
}

open class Background(
    val color: Int? = null,
    val drawable: Drawable? = null,
    val resource: Int? = null,
    val attr: Int? = null,
    child: Widget,
    key: Any? = null
) : ViewPropsWidget(child, key) {

    override fun applyViewProps(context: BuildContext, view: View) {
        when {
            color != null -> view.setBackgroundColor(color)
            drawable != null -> view.background = drawable
            resource != null -> view.setBackgroundResource(resource)
            attr != null -> view.background = AndroidContextAmbient(context)
                .drawableAttr(attr)
            else -> view.background = null
        }
    }

}