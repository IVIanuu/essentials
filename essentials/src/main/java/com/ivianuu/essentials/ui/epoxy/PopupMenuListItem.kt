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
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.ui.popup.show
import kotlinx.android.synthetic.main.es_list_widget_menu.*

fun <T> EpoxyController.PopupMenuListItem(
    id: Any?,

    items: List<PopupMenuItem<T>>,
    onItemSelected: (T) -> Unit,
    onCanceled: (() -> Unit)? = null,

    title: String? = null,
    titleRes: Int? = null,

    text: String? = null,
    textRes: Int? = null,

    icon: Drawable? = null,
    iconRes: Int? = null,

    avatar: Drawable? = null,
    avatarRes: Int? = null,

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,

    enabled: Boolean = true,

    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = ListItem(
    id = id,
    title = title,
    titleRes = titleRes,
    text = text,
    textRes = textRes,
    icon = icon,
    iconRes = iconRes,
    avatar = avatar,
    avatarRes = avatarRes,
    onClick = onClick,
    onLongClick = onLongClick,
    widgetLayoutRes = R.layout.es_list_widget_menu,
    enabled = enabled,
    builderBlock = {
        bind {
            es_list_widget_menu.setOnClickListener {
                PopupMenu(items, onCanceled, onItemSelected)
                    .show(it)
            }
        }

        builderBlock?.invoke(this)
    }
)