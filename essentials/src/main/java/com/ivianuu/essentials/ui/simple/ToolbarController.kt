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

package com.ivianuu.essentials.ui.simple

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.kommon.core.view.items
import com.ivianuu.traveler.goBack
import kotlinx.android.synthetic.main.es_app_bar_with_toolbar.es_app_bar
import kotlinx.android.synthetic.main.es_app_bar_with_toolbar.es_toolbar

/**
 * A controller which hosts a toolbar
 */
abstract class ToolbarController : EsController() {

    val appBar: AppBarLayout
        get() = es_app_bar

    val toolbar: Toolbar
        get() = es_toolbar

    protected open val toolbarTitle: String? get() = null
    protected open val toolbarTitleRes: Int get() = 0
    protected open val toolbarMenuRes: Int get() = 0
    protected open val toolbarBackButton: Boolean get() = router.rootController != this
    protected open val lightToolbar: Boolean get() = getPrimaryColor().isLight

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)

        with(toolbar) {
            when {
                toolbarTitle != null -> title = toolbarTitle
                toolbarTitleRes != 0 -> setTitle(toolbarTitleRes)
            }

            if (toolbarMenuRes != 0) {
                inflateMenu(toolbarMenuRes)
                setOnMenuItemClickListener { this@ToolbarController.onToolbarMenuItemClicked(it) }
            }

            if (toolbarBackButton) {
                setNavigationIcon(R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener { travelerRouter.goBack() }
            }

            val titleColor = getPrimaryTextColor(!lightToolbar)
            val subTitleColor = getSecondaryTextColor(!lightToolbar)
            val iconColor = getIconColor(!lightToolbar)

            setTitleTextColor(titleColor)
            setSubtitleTextColor(subTitleColor)
            navigationIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
            overflowIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

            menu.items
                .mapNotNull { it.icon }
                .forEach { it.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN) }
        }
    }

    open fun onToolbarMenuItemClicked(item: MenuItem): Boolean = false

}