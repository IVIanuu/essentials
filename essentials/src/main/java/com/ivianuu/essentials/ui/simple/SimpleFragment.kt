package com.ivianuu.essentials.ui.simple

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
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
    protected open val toolbarBackButton = true
    protected open val lightToolbar: Boolean get() = primaryColor().isLight

    protected val epoxyController by unsafeLazy { epoxyController() }

    open val coordinatorLayout
        get() =
            requireView().findViewById<CoordinatorLayout>(R.id.coordinator_layout)
    open val list
        get() =
            requireView().findViewById<EpoxyRecyclerView>(R.id.list)
    open val toolbar
        get() =
            requireView().findViewById<Toolbar>(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    @SuppressLint("PrivateResource")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(toolbar) {
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

        list.setController(epoxyController)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        list.adapter = null
        super.onDestroyView()
    }

    override fun invalidate() {
        epoxyController.requestModelBuild()
    }

    override fun onOptionsItemSelected(item: MenuItem) = super.onOptionsItemSelected(item)

    abstract fun epoxyController(): EpoxyController

}