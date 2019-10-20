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

package com.ivianuu.essentials.ui.simple

import android.graphics.PorterDuff
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.google.android.material.appbar.AppBarLayout
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.show
import com.ivianuu.essentials.util.*
import kotlinx.android.synthetic.main.es_controller_tabs.*
import kotlinx.android.synthetic.main.es_view_toolbar.*


/**
 * A controller which hosts a toolbar
 */
abstract class ToolbarController : CoordinatorController() {

    val appBar: AppBarLayout
        get() = es_app_bar

    val toolbar: Toolbar
        get() = es_toolbar

    protected open val toolbarTitle: String? get() = null
    protected open val toolbarTitleRes: Int? get() = null
    protected open val toolbarMenu: PopupMenu<*>? get() = null
    protected open val toolbarBackButton: Boolean
        get() = router.backStack.firstOrNull()?.controller != this

    protected open val lightToolbar: Boolean
        get() = requireActivity().getPrimaryColor().isLight

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        with(toolbar) {
            when {
                toolbarTitle != null -> title = toolbarTitle
                toolbarTitleRes != null -> setTitle(toolbarTitleRes!!)
            }

            val toolbarMenu = toolbarMenu?.copy(
                style = R.attr.actionOverflowMenuStyle, gravity = Gravity.END
            )

            if (toolbarMenu != null) {
                menu.add("dummy")
                overflowIcon = requireActivity().drawable(R.drawable.abc_ic_menu_overflow_material)
                val overflow = findView {
                    it is ImageView && it.drawable == overflowIcon
                }!!

                overflow.setOnClickListener { toolbarMenu.show(it) }
            }

            if (toolbarBackButton) {
                setNavigationIcon(R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener { navigator.pop() }
            }

            val titleColor = requireActivity().getPrimaryTextColor(!lightToolbar)
            val subTitleColor = requireActivity().getSecondaryTextColor(!lightToolbar)
            val iconColor = requireActivity().getIconColor(!lightToolbar)

            setTitleTextColor(titleColor)
            setSubtitleTextColor(subTitleColor)
            navigationIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
            overflowIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

            menu.children
                .mapNotNull { it.icon }
                .forEach { it.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN) }
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

}