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
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.compose.Composable
import androidx.core.view.children
import com.google.android.material.appbar.AppBarLayout
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.material.popupMenuRoute
import com.ivianuu.essentials.util.drawable
import com.ivianuu.essentials.util.getIconColor
import com.ivianuu.essentials.util.getPrimaryColor
import com.ivianuu.essentials.util.getPrimaryTextColor
import com.ivianuu.essentials.util.getSecondaryTextColor
import com.ivianuu.essentials.util.isLight
import kotlinx.android.synthetic.main.es_controller_tabs.es_app_bar
import kotlinx.android.synthetic.main.es_view_toolbar.es_toolbar

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
    protected open val toolbarMenuConfig: ToolbarMenuConfig<out Any?>? get() = null
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

            val toolbarMenuConfig = toolbarMenuConfig as? ToolbarMenuConfig<Any?>

            if (toolbarMenuConfig != null) {
                menu.add("dummy")
                overflowIcon = requireActivity().drawable(R.drawable.es_ic_more_vert)
                val overflow = findView {
                    it is ImageView && it.drawable == overflowIcon
                }!!

                overflow.setOnClickListener {
                    navigator.push(
                        popupMenuRoute(
                            view = overflow,
                            items = toolbarMenuConfig.items,
                            onSelected = toolbarMenuConfig.onSelected,
                            onCancel = toolbarMenuConfig.onCancel,
                            item = toolbarMenuConfig.item
                        )
                    )
                }
            }

            if (toolbarBackButton) {
                setNavigationIcon(R.drawable.es_ic_arrow_back)
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

    data class ToolbarMenuConfig<T>(
        val items: List<T>,
        val onSelected: (T) -> Unit,
        val onCancel: (() -> Unit)? = null,
        val item: @Composable() (T) -> Unit
    )
}
