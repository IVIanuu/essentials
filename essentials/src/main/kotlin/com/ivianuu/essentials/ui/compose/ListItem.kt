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

package com.ivianuu.essentials.ui.compose

import android.view.View
import androidx.core.view.children
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ContextAmbient
import com.ivianuu.compose.ViewById
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.ambient
import com.ivianuu.compose.set
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.getSecondaryTextColor
import kotlinx.android.synthetic.main.es_list_item.view.*

fun ComponentComposition.ListItem(
    key: Any? = null,
    title: (ComponentComposition.() -> Unit)? = null,
    text: (ComponentComposition.() -> Unit)? = null,

    leading: (ComponentComposition.() -> Unit)? = null,
    trailing: (ComponentComposition.() -> Unit)? = null,

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,

    enabled: Boolean = true
) {
    ViewByLayoutRes<View>(layoutRes = R.layout.es_list_item) {
        set(onClick) {
            if (onClick != null) {
                setOnClickListener { onClick() }
            } else {
                setOnClickListener(null)
            }
        }

        set(onLongClick) {
            if (onLongClick != null) {
                setOnLongClickListener { onLongClick(); true }
            } else {
                setOnLongClickListener(null)
            }
        }

        set(enabled) { enabled ->
            es_list_text_container.isEnabled = enabled
            es_list_text_container.children.forEach {
                it.isEnabled = enabled
            }

            es_list_leading.isEnabled = enabled
            es_list_leading.children.forEach {
                it.isEnabled = enabled
            }

            es_list_trailing.isEnabled = enabled
            es_list_trailing.children.forEach {
                it.isEnabled = enabled
            }

            isEnabled = enabled
        }

        ViewById<View>(id = R.id.es_list_text_container) {
            if (title != null) {
                TextStyle(textAppearance = R.style.TextAppearance_MaterialComponents_Subtitle1) {
                    title()
                }
            }

            if (text != null) {
                TextStyle(
                    textAppearance = R.style.TextAppearance_AppCompat_Body2,
                    textColor = ambient(ContextAmbient).getSecondaryTextColor()
                ) {
                    text()
                }
            }
        }

        ViewById<View>(id = R.id.es_list_leading) {
            leading?.invoke(composition)
        }

        ViewById<View>(id = R.id.es_list_trailing) {
            trailing?.invoke(composition)
        }
    }
}
