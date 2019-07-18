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

package com.ivianuu.essentials.sample.ui.widget

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.view.inflate

class Icon(
    val icon: Drawable? = null,
    val iconRes: Int? = null,
    override val key: Any? = null
) : Widget<ImageView>() {

    override val viewId: Int
        get() = R.id.es_list_icon

    override fun createView(container: ViewGroup): ImageView =
        container.inflate<ImageView>(R.layout.es_list_action_icon)

    init {
        state(icon, iconRes)
    }

    override fun bind(view: ImageView) {
        super.bind(view)
        when {
            icon != null -> view.setImageDrawable(icon)
            iconRes != null -> view.setImageResource(iconRes)
            else -> view.setImageDrawable(null)
        }
    }
}