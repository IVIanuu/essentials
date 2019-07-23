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

package com.ivianuu.essentials.sample.ui.widget.material

import android.graphics.drawable.Drawable
import com.google.android.material.button.MaterialButton
import com.ivianuu.essentials.sample.ui.widget.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget

class MaterialButtonWidget(
    val text: String? = null,
    val textRes: Int? = null,

    val icon: Drawable? = null,
    val iconRes: Int? = null,

    val onClick: (() -> Unit)? = null,
    key: Any? = null
) : ViewWidget<MaterialButton>(key) {

    override fun createView(context: BuildContext): MaterialButton =
        MaterialButton(AndroidContextAmbient(context))

    override fun updateView(context: BuildContext, view: MaterialButton) {
        super.updateView(context, view)

        when {
            text != null -> view.text = text
            textRes != null -> view.setText(textRes)
            else -> view.text = null
        }

        when {
            icon != null -> view.icon = icon
            iconRes != null -> view.setIconResource(iconRes)
            else -> view.icon = null
        }

        if (onClick != null) {
            view.setOnClickListener { onClick!!() }
        } else {
            view.setOnClickListener(null)
        }

        view.isEnabled = onClick != null
    }
}