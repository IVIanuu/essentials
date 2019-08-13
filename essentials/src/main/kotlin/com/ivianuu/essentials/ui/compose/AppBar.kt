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

import android.graphics.PorterDuff
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import com.google.android.material.appbar.MaterialToolbar
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.layoutRes
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.navigation.navigator
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.show
import com.ivianuu.essentials.util.getIconColor
import com.ivianuu.essentials.util.getPrimaryTextColor
import com.ivianuu.essentials.util.getSecondaryTextColor
import com.ivianuu.kommon.core.view.drawable

fun ComponentComposition.AppBar(
    title: String? = null,
    titleRes: Int? = null,
    menu: PopupMenu<*>? = null,
    showBackButton: Boolean? = null,
    light: Boolean? = null
) {
    val navigator = navigator
    val finalShowBackButton = showBackButton ?: (navigator.backStack.size > 1)
    val light = false //getPrimaryColor().isLight // todo

    View<MaterialToolbar> {
        layoutRes(R.layout.es_app_bar)

        bindView {
            when {
                title != null -> this.title = title
                titleRes != null -> setTitle(titleRes)
            }

            val finalMenu = menu?.copy(
                style = R.attr.actionOverflowMenuStyle, gravity = Gravity.END
            )

            if (finalMenu != null) {
                this.menu.add("dummy")
                overflowIcon = drawable(R.drawable.abc_ic_menu_overflow_material)
                val overflow = findView {
                    it is ImageView && it.drawable == overflowIcon
                }!!

                overflow.setOnClickListener { finalMenu.show(it) }
            }

            if (finalShowBackButton) {
                setNavigationIcon(R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener { navigator.pop() }
            }

            val titleColor = getPrimaryTextColor(!light)
            val subTitleColor = getSecondaryTextColor(!light)
            val iconColor = getIconColor(!light)

            setTitleTextColor(titleColor)
            setSubtitleTextColor(subTitleColor)
            navigationIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
            overflowIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

            this.menu.children
                .mapNotNull { it.icon }
                .forEach { it.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN) }
        }
    }
}

private fun View.findView(predicate: (View) -> Boolean): View? {
    if (predicate(this)) return this
    if (this !is ViewGroup) return null

    for (i in 0 until childCount) {
        val child = getChildAt(i)
        val view = child.findView(predicate)
        if (view != null) return view
    }

    return null
}