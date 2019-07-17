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

package com.ivianuu.essentials.ui.epoxy

import android.graphics.drawable.Drawable
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.andTrue
import com.ivianuu.kommon.core.view.inflate
import kotlinx.android.synthetic.main.es_list_item.*
import kotlinx.android.synthetic.main.es_list_item.view.*

fun EpoxyController.ListItem(
    id: Any?,

    state: Array<Any?>? = null,

    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    icon: Drawable? = null,
    iconRes: Int? = null,

    avatar: Drawable? = null,
    avatarRes: Int? = null,

    widgetLayoutRes: Int? = null,

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,

    enabled: Boolean = true,

    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = model {
    id(id)

    state(title, titleRes)
    state(text, textRes)
    state(icon != null, iconRes)
    state(avatar != null, avatarRes)
    state(onClick != null, onLongClick != null)
    state(enabled)
    state(builderBlock != null)
    if (state != null) state(*state)

    buildView {
        val view = it.inflate(R.layout.es_list_item)
        if (widgetLayoutRes != null) {
            view.es_list_widget_container.inflate(widgetLayoutRes, true)
        }

        return@buildView view
    }

    bind {
        when {
            title != null -> {
                es_list_title.text = title
                es_list_title.isVisible = true
            }
            titleRes != null -> {
                es_list_title.setText(titleRes)
                es_list_title.isVisible = true
            }
            else -> {
                es_list_title.text = null
                es_list_title.isVisible = false
            }
        }
        es_list_title.isEnabled = enabled

        when {
            text != null -> {
                es_list_text.text = text
                es_list_text.isVisible = true
            }
            textRes != null -> {
                es_list_text.setText(textRes)
                es_list_text.isVisible = true
            }
            else -> {
                es_list_text.text = null
                es_list_text.isVisible = false
            }
        }
        es_list_text.isEnabled = enabled

        when {
            icon != null -> {
                es_list_icon.setImageDrawable(icon)
                es_list_icon.isVisible = true
            }
            iconRes != null -> {
                es_list_icon.setImageResource(iconRes)
                es_list_icon.isVisible = true
            }
            else -> {
                es_list_icon.setImageDrawable(null)
                es_list_icon.isVisible = false
            }
        }
        es_list_icon.isEnabled = enabled

        when {
            avatar != null -> {
                es_list_avatar.setImageDrawable(avatar)
                es_list_avatar.isVisible = true
            }
            avatarRes != null -> {
                es_list_avatar.setImageResource(avatarRes)
                es_list_avatar.isVisible = true
            }
            else -> {
                es_list_avatar.setImageDrawable(null)
                es_list_avatar.isVisible = false
            }
        }
        es_list_avatar.isEnabled = enabled

        es_list_image_frame.isVisible =
            es_list_icon.isVisible || es_list_avatar.isVisible

        es_list_widget_container.isEnabled = enabled
        (0 until es_list_widget_container.childCount)
            .map { es_list_widget_container.getChildAt(it) }
            .forEach { it.isEnabled = enabled }

        if (onClick != null) {
            root.setOnClickListener { onClick() }
        } else {
            root.setOnClickListener(null)
        }

        if (onLongClick != null) {
            root.setOnLongClickListener { onLongClick().andTrue() }
        } else {
            root.setOnLongClickListener(null)
        }

        root.isEnabled = enabled && (onClick != null || onLongClick != null)
    }

    builderBlock?.invoke(this)
}