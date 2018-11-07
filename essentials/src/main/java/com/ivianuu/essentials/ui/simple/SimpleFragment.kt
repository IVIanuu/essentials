package com.ivianuu.essentials.ui.simple

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.isInBackstack
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.DiffResult
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.ivianuu.androidktx.core.view.items
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.iconColor
import com.ivianuu.essentials.util.ext.isLight
import com.ivianuu.essentials.util.ext.primaryColor
import com.ivianuu.essentials.util.ext.primaryTextColor
import com.ivianuu.essentials.util.ext.secondaryTextColor
import com.ivianuu.traveler.goBack

/**
 * Simple fragment
 */
abstract class SimpleFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_simple

    protected open val toolbarTitle: String? = null
    protected open val toolbarTitleRes = 0
    protected open val toolbarMenuRes = 0
    protected open val toolbarBackButton get() = isInBackstack
    protected open val lightToolbar: Boolean get() = primaryColor().isLight

    val optionalEpoxyController: EpoxyController? get() = _epoxyController
    val epoxyController
        get() = optionalEpoxyController
            ?: throw IllegalStateException("no epoxy controller instantiated")

    private var _epoxyController: EpoxyController? = null

    val appBar get() = optionalAppBar ?: throw IllegalStateException("no app bar layout found")

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*try {
            epoxyController.onRestoreInstanceState(savedInstanceState)
        } catch (e: Exception) {
        }*/

        savedInstanceState?.let {
            layoutManagerState = it.getParcelable(KEY_LAYOUT_MANAGER_STATE)
        }
    }

    @SuppressLint("PrivateResource")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optionalToolbar?.run {
            when {
                toolbarTitle != null -> title = toolbarTitle
                toolbarTitleRes != 0 -> setTitle(toolbarTitleRes)
            }

            if (toolbarMenuRes != 0) {
                inflateMenu(toolbarMenuRes)
                setOnMenuItemClickListener { onOptionsItemSelected(it) }
            }

            if (toolbarBackButton) {
                setNavigationIcon(R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener { router.goBack() }
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
            _epoxyController = epoxyController()?.apply {
                addModelBuildListener(modelBuiltListener)
                setController(this)
            }
            this@SimpleFragment.layoutManager()?.let { layoutManager = it }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        /*try {
            epoxyController.onSaveInstanceState(outState)
        } catch (e: Exception) {
        }*/

        if (view != null) {
            layoutManagerState = optionalRecyclerView?.layoutManager?.onSaveInstanceState()
        }

        outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, layoutManagerState)
    }

    override fun onDestroyView() {
        layoutManagerState = optionalRecyclerView?.layoutManager?.onSaveInstanceState()
        _epoxyController?.let {
            it.cancelPendingModelBuild()
            it.removeModelBuildListener(modelBuiltListener)
        }
        _epoxyController = null
        super.onDestroyView()
    }

    override fun invalidate() {
        _epoxyController?.requestModelBuild()
    }

    protected open fun epoxyController(): EpoxyController? = null

    protected open fun layoutManager(): RecyclerView.LayoutManager? = null

    companion object {
        private const val KEY_LAYOUT_MANAGER_STATE = "SimpleFragment.layoutManagerState"
    }
}