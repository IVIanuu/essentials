package com.ivianuu.essentials.ui.director

import android.content.Context
import android.view.View
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.effect.EffectContext
import com.ivianuu.essentials.util.ContextAware
import kotlinx.android.extensions.LayoutContainer

interface ControllerContext : ContextAware, EffectContext, LayoutContainer {
    val controller: EsController

    override val providedContext: Context
        get() = controller.providedContext

    override val containerView: View?
        get() = controller.view

    fun invalidate()

    fun onDetachView(callback: (View) -> Unit)

}