/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.util.ext.tint

import android.graphics.drawable.Drawable
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import com.ivianuu.essentials.util.ext.getField
import com.ivianuu.essentials.util.ext.getIconColor
import com.ivianuu.essentials.util.ext.isDark
import com.ivianuu.essentials.util.ext.tintedNullable

fun Toolbar.tint(bgColor: Int,
                 itemColor: Int = context.getIconColor(bgColor.isDark)) {
    setBackgroundColor(bgColor)
    setItemColor(itemColor)
}

fun Toolbar.setItemColor(itemColor: Int) {
    setTitleTextColor(itemColor)
    overflowIcon?.tintedNullable(itemColor)
    navigationIcon?.tintedNullable(itemColor)

    // The collapse icon displays when action views are expanded (e.g. SearchView)
    try {
        val field = Toolbar::class.getField("mCollapseIcon")
        (field.get(this) as Drawable?)?.tintedNullable(
            itemColor)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    (0 until menu.size())
        .map { menu.getItem(it) }
        .forEach {
            it.icon?.tintedNullable(itemColor)
            val actionView = it.actionView
            if (actionView != null && actionView is SearchView) {
                actionView.setItemColor(itemColor)
            }
        }
}