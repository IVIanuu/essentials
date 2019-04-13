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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.common.EsRecyclerView
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.kommon.core.view.items
import com.ivianuu.list.ItemController
import com.ivianuu.traveler.goBack

/**
 * Simple controller
 */
abstract class SimpleController : EsController() {

    override val layoutRes: Int
        get() = R.layout.es_controller_simple

    protected open val toolbarTitle: String? get() = null
    protected open val toolbarTitleRes: Int get() = 0
    protected open val toolbarMenuRes: Int get() = 0
    protected open val toolbarBackButton: Boolean get() = router.rootController != this
    protected open val lightToolbar: Boolean get() = getPrimaryColor().isLight

    val optionalItemController: ItemController? by lazy { itemController() }
    val itemController
        get() = optionalItemController
            ?: error("no model controller instantiated")

    val appBar get() = optionalAppBar ?: error("no app bar layout found")
    open val optionalAppBar: AppBarLayout?
        get() = containerView?.findViewById(R.id.es_app_bar)

    val coordinatorLayout
        get() = optionalCoordinatorLayout
            ?: error("no coordinator layout found")

    open val optionalCoordinatorLayout: CoordinatorLayout?
        get() = containerView?.findViewById(R.id.es_coordinator_layout)

    val recyclerView
        get() = optionalRecyclerView ?: error("no recycler view found")

    open val optionalRecyclerView: EsRecyclerView?
        get() = containerView?.findViewById(R.id.es_recycler_view)

    val toolbar
        get() = optionalToolbar ?: error("no toolbar found")

    open val optionalToolbar: Toolbar?
        get() = containerView?.findViewById(R.id.es_toolbar)

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)

        optionalToolbar?.run {
            when {
                toolbarTitle != null -> title = toolbarTitle
                toolbarTitleRes != 0 -> setTitle(toolbarTitleRes)
            }

            if (toolbarMenuRes != 0) {
                inflateMenu(toolbarMenuRes)
                setOnMenuItemClickListener { this@SimpleController.onToolbarMenuItemClicked(it) }
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

        optionalRecyclerView?.run {
            adapter = optionalItemController?.adapter
            this@SimpleController.layoutManager()?.let { layoutManager = it }
        }
    }

    override fun onDestroyView(view: View) {
        optionalItemController?.cancelPendingItemBuild()
        super.onDestroyView(view)
    }

    override fun invalidate() {
        optionalItemController?.requestItemBuild()
    }

    protected open fun itemController(): ItemController? = null

    protected open fun layoutManager(): RecyclerView.LayoutManager? = null

    protected open fun onToolbarMenuItemClicked(item: MenuItem): Boolean = false

}