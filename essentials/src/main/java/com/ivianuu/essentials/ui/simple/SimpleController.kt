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

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.DiffResult
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.ivianuu.androidktx.appcompat.widget.menuIconColor
import com.ivianuu.androidktx.appcompat.widget.navigationIconColor
import com.ivianuu.androidktx.appcompat.widget.navigationIconResource
import com.ivianuu.androidktx.appcompat.widget.overflowIconColor
import com.ivianuu.androidktx.appcompat.widget.subtitleTextColor
import com.ivianuu.androidktx.appcompat.widget.titleResource
import com.ivianuu.androidktx.appcompat.widget.titleTextColor
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.util.ext.iconColor
import com.ivianuu.essentials.util.ext.isLight
import com.ivianuu.essentials.util.ext.primaryColor
import com.ivianuu.essentials.util.ext.primaryTextColor
import com.ivianuu.essentials.util.ext.secondaryTextColor

/**
 * Simple controller
 */
abstract class SimpleController : BaseController() {

    override val layoutRes = R.layout.fragment_simple

    protected open val toolbarTitle: String? = null
    protected open val toolbarTitleRes = 0
    protected open val toolbarMenuRes = 0
    protected open val toolbarBackButton
        get() = router.backstack.firstOrNull()?.controller != this
    protected open val lightToolbar: Boolean get() = primaryColor().isLight

    protected val epoxyController get() = _epoxyController ?: throw IllegalStateException()
    private var _epoxyController: EpoxyController? = null

    val appBar get() = optionalAppBar ?: throw IllegalStateException("no app bar layout")

    open val optionalAppBar: AppBarLayout?
        get() = view?.findViewById(R.id.app_bar)

    val coordinatorLayout
        get() = optionalCoordinatorLayout
            ?: throw IllegalStateException("no coordinator layout found")

    open val optionalCoordinatorLayout: CoordinatorLayout?
        get() = view?.findViewById(R.id.coordinator_layout)

    val recyclerView
        get() = optionalRecyclerView ?: throw IllegalStateException("no recycler view found")

    open val optionalRecyclerView: EpoxyRecyclerView?
        get() = view?.findViewById(R.id.recycler_view)

    val toolbar
        get() = optionalToolbar ?: throw IllegalStateException("no toolbar found")

    open val optionalToolbar: Toolbar?
        get() = view?.findViewById(R.id.toolbar)

    private val modelBuiltListener: (DiffResult) -> Unit = {
        if (layoutManagerState != null) {
            optionalRecyclerView?.layoutManager?.onRestoreInstanceState(layoutManagerState)
            layoutManagerState = null
        }
    }

    private var layoutManagerState: Parcelable? = null

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        super.onRestoreViewState(view, savedViewState)

        /*try {
            epoxyController.onRestoreInstanceState(savedInstanceState)
        } catch (e: Exception) {
        }*/

        layoutManagerState = savedViewState.getParcelable(KEY_LAYOUT_MANAGER_STATE)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        optionalToolbar?.run {
            when {
                toolbarTitle != null -> title = toolbarTitle
                toolbarTitleRes != 0 -> titleResource = toolbarTitleRes
            }

            if (toolbarMenuRes != 0) {
                inflateMenu(toolbarMenuRes)
                setOnMenuItemClickListener { onOptionsItemSelected(it) }
            }

            if (toolbarBackButton) {
                navigationIconResource = R.drawable.abc_ic_ab_back_material
                goBackOnNavigationClick()
            }

            val titleColor = primaryTextColor(!lightToolbar)
            val subTitleColor = secondaryTextColor(!lightToolbar)
            val iconColor = iconColor(!lightToolbar)

            titleTextColor = titleColor
            subtitleTextColor = subTitleColor
            navigationIconColor = iconColor
            overflowIconColor = iconColor
            menuIconColor = iconColor
        }

        optionalRecyclerView?.run {
            _epoxyController = epoxyController().apply {
                addModelBuildListener(modelBuiltListener)
                setController(this)
            }
            this@SimpleController.layoutManager()?.let { layoutManager = it }
        }
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        layoutManagerState = optionalRecyclerView?.layoutManager?.onSaveInstanceState()
        outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, layoutManagerState)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        _epoxyController?.let {
            it.cancelPendingModelBuild()
            it.removeModelBuildListener(modelBuiltListener)
        }
        _epoxyController = null
    }

    override fun invalidate() {
        _epoxyController?.requestModelBuild()
    }

    protected open fun epoxyController(): EpoxyController = EmptyEpoxyController

    protected open fun layoutManager(): RecyclerView.LayoutManager? = null

    companion object {
        private const val KEY_LAYOUT_MANAGER_STATE = "SimpleFragment.layoutManagerState"
    }
}