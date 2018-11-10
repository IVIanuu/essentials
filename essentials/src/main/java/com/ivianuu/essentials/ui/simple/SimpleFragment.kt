package com.ivianuu.essentials.ui.simple

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.isInBackstack
import androidx.recyclerview.widget.RecyclerView
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

    override val layoutRes get() = R.layout.fragment_simple

    protected open val toolbarTitle: String? get() = null
    protected open val toolbarTitleRes get() = 0
    protected open val toolbarMenuRes get() = 0
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
            _epoxyController = epoxyController()?.also { setController(it) }
            this@SimpleFragment.layoutManager()?.let { layoutManager = it }
        }
    }

    override fun onDestroyView() {
        _epoxyController?.cancelPendingModelBuild()
        _epoxyController = null
        super.onDestroyView()
    }

    override fun invalidate() {
        _epoxyController?.requestModelBuild()
    }

    protected open fun epoxyController(): EpoxyController? = null

    protected open fun layoutManager(): RecyclerView.LayoutManager? = null

}