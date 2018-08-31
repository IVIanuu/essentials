package com.ivianuu.essentials.ui.simple

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.isInBackstack
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
    protected open val toolbarBackButton get() = isInBackstack
    protected open val lightToolbar: Boolean get() = primaryColor().isLight

    protected val epoxyController by unsafeLazy { epoxyController() }

    val appBar
        get() = _appBar ?: throw IllegalStateException("no app bar layout")
    private val _appBar: AppBarLayout?
        get() = view?.findViewById(R.id.app_bar)

    val coordinatorLayout
        get() =
            _coordinatorLayout ?: throw IllegalStateException("no coordinator layout found")
    private val _coordinatorLayout: CoordinatorLayout?
        get() = view?.findViewById(R.id.coordinator_layout)

    val list
        get() =
            _list ?: throw IllegalStateException("no list found")
    private val _list: EpoxyRecyclerView?
        get() = view?.findViewById(R.id.list)

    val toolbar
        get() =
            _toolbar ?: throw IllegalStateException("no toolbar found")
    private val _toolbar: Toolbar?
        get() = view?.findViewById(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    @SuppressLint("PrivateResource")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _toolbar?.run {
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

        _list?.setController(epoxyController)
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

    abstract fun epoxyController(): EpoxyController

}