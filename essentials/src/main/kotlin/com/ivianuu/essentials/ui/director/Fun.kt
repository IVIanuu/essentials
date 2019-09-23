package com.ivianuu.essentials.ui.director

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.Controller
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.effect.EffectContext
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.util.Properties
import com.ivianuu.essentials.util.viewLifecycleScope
import com.ivianuu.kommon.core.view.inflate
import kotlinx.android.synthetic.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun controller(
        layoutRes: Int,
        render: ControllerContext.(View) -> Unit = {}
): Controller = controller(createView = { it.inflate(layoutResId = layoutRes) }, render = render)

fun controllerRoute(
        extras: Properties = Properties(),
        options: ControllerRoute.Options? = null,
        layoutRes: Int,
        render: ControllerContext.(View) -> Unit
) = controllerRoute(
        extras = extras,
        options = options,
        factory = { controller(layoutRes = layoutRes, render = render) }
)

fun controller(
        createView: (ViewGroup) -> View,
        render: ControllerContext.(View) -> Unit = {}
): Controller = FunController(createView = createView, render = render)

fun controllerRoute(
        extras: Properties = Properties(),
        options: ControllerRoute.Options? = null,
        createView: (ViewGroup) -> View,
        render: ControllerContext.(View) -> Unit
) = controllerRoute(
        extras = extras,
        options = options,
        factory = { controller(createView = createView, render = render) }
)

private class FunController(
        private val createView: (ViewGroup) -> View,
        private val render: ControllerContext.(View) -> Unit
) : EsController() {

    private val controllerContext = FunControllerContext()
    private val detachViewActions = mutableListOf<(View) -> Unit>()

    private var invalidateJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View =
            createView(container)

    override fun onAttach(view: View) {
        super.onAttach(view)
        renderView()
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        invalidateJob?.cancel()
        detachViewActions.forEach { it(view) }
        detachViewActions.clear()
        controllerContext.clearFindViewByIdCache()
    }

    private fun postRenderView() {
        invalidateJob?.cancel()
        invalidateJob = viewLifecycleScope.launch { renderView() }
    }

    private fun renderView() {
        val view = view ?: error("view not attached")
        render(controllerContext, view)
    }

    private inner class FunControllerContext : ControllerContext, EffectContext by EffectContext() {

        override val controller: EsController
            get() = this@FunController

        override fun invalidate() {
            postRenderView()
        }

        override fun onDetachView(callback: (View) -> Unit) {
            detachViewActions += callback
        }
    }
}