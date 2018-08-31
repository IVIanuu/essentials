package com.ivianuu.essentials.ui.simple

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.isInBackstack
import android.support.v7.widget.Toolbar
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.state.StateFragment
import com.ivianuu.essentials.util.ext.*

/**
 * Simple fragment
 */
abstract class SimpleFragment : StateFragment() {

    override val layoutRes = R.layout.fragment_simple

    protected open val toolbarMenuRes = 0
    protected open val toolbarBackButton get() = isInBackstack
    protected open val lightToolbar: Boolean get() = primaryColor().isLight

    protected val epoxyController by unsafeLazy { epoxyController() }

    val appBar get() = optionalAppBar ?: throw IllegalStateException("no app bar layout")

    val optionalAppBar: AppBarLayout?
        get() = view?.findViewById(R.id.app_bar)

    val coordinatorLayout
        get() = optionalCoordinatorLayout
            ?: throw IllegalStateException("no coordinator layout found")

    val optionalCoordinatorLayout: CoordinatorLayout?
        get() = view?.findViewById(R.id.coordinator_layout)

    val recyclerView
        get() = optionalRecyclerView ?: throw IllegalStateException("no recycler view found")

    val optionalRecyclerView: EpoxyRecyclerView?
        get() = view?.findViewById(R.id.recycler_view)

    val toolbar
        get() = optionalToolbar ?: throw IllegalStateException("no toolbar found")

    val optionalToolbar: Toolbar?
        get() = view?.findViewById(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    @SuppressLint("PrivateResource")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optionalToolbar?.run {
            if (toolbarMenuRes != 0) {
                inflateMenu(toolbarMenuRes)
                setOnMenuItemClickListener { onOptionsItemSelected(it) }
            }

            if (toolbarBackButton) {
                setNavigationIcon(R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener { router.exit() }
            }

            val titleColor = primaryTextColor(!lightToolbar)
            val subTitleColor = secondaryTextColor(!lightToolbar)

            setTitleTextColor(titleColor)
            setSubtitleTextColor(subTitleColor)
            navigationIcon?.tint(subTitleColor)
            overflowIcon?.tint(subTitleColor)
            menu.items
                .forEach { it.icon?.tint(subTitleColor) }
        }

        optionalRecyclerView?.setController(epoxyController)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }

    override fun invalidate() {
        epoxyController.requestModelBuild()
    }

    protected open fun epoxyController(): EpoxyController = epoxyController {
    }

}