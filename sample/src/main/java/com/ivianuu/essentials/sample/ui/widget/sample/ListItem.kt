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

package com.ivianuu.essentials.sample.ui.widget.sample

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.layout.IdViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.layout.InflateViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.sample.ui.widget.view.TextStyleAmbient
import com.ivianuu.essentials.util.andTrue

fun ListItem(
    title: (BuildContext.() -> Unit)? = null,
    subtitle: (BuildContext.() -> Unit)? = null,

    leading: (BuildContext.() -> Unit)? = null,
    trailing: (BuildContext.() -> Unit)? = null,

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,

    key: Any? = null
): Widget {
    return InflateViewGroupWidget<ConstraintLayout>(
        layoutRes = R.layout.list_item,
        key = key,
        updateView = { view ->
            if (onClick != null) {
                view.setOnClickListener { onClick!!() }
            } else {
                view.setOnClickListener(null)
            }

            if (onLongClick != null) {
                view.setOnLongClickListener { onLongClick!!().andTrue() }
            } else {
                view.setOnLongClickListener(null)
            }

            view.isEnabled = onClick != null || onLongClick != null
        },
        children = {
            if (leading != null) {
                +IdViewGroupWidget<ViewGroup>(
                    R.id.leading_container,
                    children = leading
                )
            }
            if (title != null || subtitle != null) {
                +IdViewGroupWidget<ViewGroup>(R.id.text_container) {
                    if (title != null) {
                        +TextStyleAmbient.Provider(R.style.TextAppearance_MaterialComponents_Subtitle1) {
                            title()
                        }
                    }
                    if (subtitle != null) {
                        +TextStyleAmbient.Provider(R.style.TextAppearance_MaterialComponents_Body2) {
                            subtitle()
                        }
                    }
                }

            }
            if (trailing != null) {
                +IdViewGroupWidget<ViewGroup>(
                    R.id.trailing_container,
                    children = trailing
                )
            }
        }
    )
}