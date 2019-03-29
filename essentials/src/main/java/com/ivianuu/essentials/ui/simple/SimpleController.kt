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
import com.ivianuu.essentials.util.ext.iconColor
import com.ivianuu.essentials.util.ext.isLight
import com.ivianuu.essentials.util.ext.primaryColor
import com.ivianuu.essentials.util.ext.primaryTextColor
import com.ivianuu.essentials.util.ext.rootTransaction
import com.ivianuu.essentials.util.ext.secondaryTextColor
import com.ivianuu.kommon.core.view.items
import com.ivianuu.list.ModelController
import com.ivianuu.traveler.goBack

/**
 * Simple controller
 */
abstract class SimpleController : EsController() {

    override val layoutRes get() = R.layout.es_controller_simple

    protected open val toolbarTitle: String? get() = null
    protected open val toolbarTitleRes get() = 0
    protected open val toolbarMenuRes get() = 0
    protected open val toolbarBackButton get() = router.rootTransaction?.controller != this
    protected open val lightToolbar: Boolean get() = primaryColor().isLight

    val optionalModelController: ModelController? get() = _modelController
    val modelController
        get() = optionalModelController
            ?: error("no epoxy controller instantiated")

    private var _modelController: ModelController? = null

    val appBar get() = optionalAppBar ?: error("no app bar layout found")

    open val optionalAppBar: AppBarLayout?
        get() = view?.findViewById(R.id.es_app_bar)

    val coordinatorLayout
        get() = optionalCoordinatorLayout
            ?: error("no coordinator layout found")

    open val optionalCoordinatorLayout: CoordinatorLayout?
        get() = view?.findViewById(R.id.es_coordinator_layout)

    val recyclerView
        get() = optionalRecyclerView ?: error("no recycler view found")

    open val optionalRecyclerView: EsRecyclerView?
        get() = view?.findViewById(R.id.es_recycler_view)

    val toolbar
        get() = optionalToolbar ?: error("no toolbar found")

    open val optionalToolbar: Toolbar?
        get() = view?.findViewById(R.id.es_toolbar)

    override fun onBindView(view: View, savedViewState: Bundle?) {
        super.onBindView(view, savedViewState)

        optionalToolbar?.run {
            when {
                toolbarTitle != null -> title = toolbarTitle
                toolbarTitleRes != 0 -> setTitle(toolbarTitleRes)
            }

            if (toolbarMenuRes != 0) {
                inflateMenu(toolbarMenuRes)
                setOnMenuItemClickListener(this@SimpleController::onToolbarMenuItemClicked)
            }

            if (toolbarBackButton) {
                setNavigationIcon(R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener { travelerRouter.goBack() }
            }

            val titleColor = primaryTextColor(!lightToolbar)
            val subTitleColor = secondaryTextColor(!lightToolbar)
            val iconColor = iconColor(!lightToolbar)

            setTitleTextColor(titleColor)
            setSubtitleTextColor(subTitleColor)
            navigationIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
            overflowIcon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

            menu.items
                .mapNotNull { it.icon }
                .forEach { it.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN) }
        }

        optionalRecyclerView?.run {
            _modelController = modelController()?.also {
                adapter = it.adapter
            }
            this@SimpleController.layoutManager()?.let { layoutManager = it }
        }
    }

    override fun onUnbindView(view: View) {
        _modelController?.cancelPendingModelBuild()
        _modelController = null
        super.onUnbindView(view)
    }

    override fun invalidate() {
        _modelController?.requestModelBuild()
    }

    protected open fun modelController(): ModelController? = null

    protected open fun layoutManager(): RecyclerView.LayoutManager? = null

    protected open fun onToolbarMenuItemClicked(item: MenuItem): Boolean = false

}