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

package com.ivianuu.essentials.ui.popup

import android.graphics.drawable.Drawable
import android.view.View
import com.github.zawadz88.materialpopupmenu.popupMenu

class PopupMenu<T>(
    val items: List<PopupMenuItem<T>>,
    val onCanceled: (() -> Unit)? = null,
    val onSelected: (T) -> Unit
)

data class PopupMenuItem<T>(
    val value: T,
    val title: String? = null,
    val titleRes: Int? = null,
    val icon: Drawable? = null,
    val iconRes: Int? = null
)

fun <T> PopupMenu<T>.show(view: View) {
    var itemSelected = false

    val androidPopupMenu = popupMenu {
        section {
            items.forEach { item ->
                item {
                    label = item.title
                    labelRes = item.titleRes ?: 0
                    iconDrawable = item.icon
                    icon = item.iconRes ?: 0
                    callback = {
                        itemSelected = true
                        onSelected(item.value)
                    }
                }
            }
        }
    }

    if (onCanceled != null) {
        androidPopupMenu.setOnDismissListener {
            if (!itemSelected) onCanceled.invoke()
        }
    }

    androidPopupMenu.show(view.context, view)
}