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
import com.ivianuu.compose.ContextAmbient
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.common.RouteAmbient
import com.ivianuu.compose.set
import com.ivianuu.compose.setBy
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.show
import com.ivianuu.essentials.util.getIconColor
import com.ivianuu.essentials.util.getPrimaryColor
import com.ivianuu.essentials.util.getPrimaryTextColor
import com.ivianuu.essentials.util.getSecondaryTextColor
import com.ivianuu.essentials.util.isLight
import com.ivianuu.kommon.core.view.drawable

fun ComponentComposition.AppBar(
    title: String? = null,
    titleRes: Int? = null,
    menu: PopupMenu<*>? = null,
    showBackButton: Boolean? = null,
    light: Boolean = ambient(ContextAmbient).getPrimaryColor().isLight
) {
    val navigator = ambient(NavigatorAmbient)

    var finalShowBackButton = showBackButton
    if (finalShowBackButton == null) {
        val route = ambient(RouteAmbient)
        finalShowBackButton = navigator.backStack.indexOf(route) > 0
    }

    ViewByLayoutRes<MaterialToolbar>(layoutRes = R.layout.es_app_bar) {
        setBy(title, titleRes) {
            when {
                title != null -> this.title = title
                titleRes != null -> setTitle(titleRes)
            }
        }
        set(menu) {
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
            } else {
                this.menu.clear()
                overflowIcon = null
            }
        }
        set(finalShowBackButton) {
            if (it) {
                setNavigationIcon(R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener { navigator.pop() }
            } else {
                navigationIcon = null
                setNavigationOnClickListener(null)
            }
        }

        set(light) {
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