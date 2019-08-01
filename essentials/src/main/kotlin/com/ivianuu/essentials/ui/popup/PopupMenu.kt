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

import android.view.Gravity
import android.view.View
import com.ivianuu.essentials.R

data class PopupMenu<T>(
    val gravity: Int = Gravity.NO_GRAVITY,
    val style: Int = R.attr.popupMenuStyle,
    val items: List<PopupMenuItem<T>>,
    val onCanceled: (() -> Unit)? = null,
    val onSelected: ((T) -> Unit)? = null
)

data class PopupMenuItem<T>(
    val value: T,
    val title: String? = null,
    val titleRes: Int? = null,
    val onSelected: (() -> Unit)? = null
)

fun <T> PopupMenu<T>.show(view: View) {
    var itemSelected = false

    val androidPopupMenu = androidx.appcompat.widget.PopupMenu(
        view.context, view, gravity, style, 0
    )

    val itemsByMenuItem = items
        .map { it to androidPopupMenu.menu.add("") }
        .onEach { (item, androidItem) ->
            when {
                item.title != null -> androidItem.title = item.title
                item.titleRes != null -> androidItem.setTitle(item.titleRes)
                else -> error("no title specified")
            }
        }
        .associateBy { it.second }
        .mapValues { it.value.first }

    androidPopupMenu.setOnMenuItemClickListener { androidItem ->
        itemSelected = true
        val item = itemsByMenuItem.getValue(androidItem)
        item.onSelected?.invoke()
        onSelected?.invoke(item.value)
        return@setOnMenuItemClickListener true
    }

    if (onCanceled != null) {
        androidPopupMenu.setOnDismissListener {
            if (!itemSelected) onCanceled.invoke()
        }
    }

    androidPopupMenu.show()
}