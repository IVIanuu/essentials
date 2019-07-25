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
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget.view.Clickable

fun BuildContext.MaterialButton(
    text: String? = null,
    textRes: Int? = null,
    icon: Drawable? = null,
    iconRes: Int? = null,
    onClick: (() -> Unit)? = null
) = StatelessWidget(id = "md_button") {
    Clickable(
        onClick = onClick ?: {},
        child = {
            +ViewWidget<MaterialButton> { view ->
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

                view.isEnabled = onClick != null
            }
        }
    )
}