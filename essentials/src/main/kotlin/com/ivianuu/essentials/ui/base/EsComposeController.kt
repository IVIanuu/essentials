package com.ivianuu.essentials.ui.base

import android.view.View
import androidx.compose.Composable
import androidx.compose.CompositionContext
import androidx.compose.disposeComposition
import androidx.ui.core.setContent
import com.ivianuu.essentials.R
import kotlinx.android.synthetic.main.es_controller_compose.*

abstract class EsComposeController : EsController() {

    override val layoutRes: Int
        get() = R.layout.es_controller_compose

    var compositionContext: CompositionContext? = null
        private set

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        compositionContext = content.setContent { compose() }
    }

    override fun onDestroyView(view: View) {
        content.disposeComposition()
        compositionContext = null
        super.onDestroyView(view)
    }

    @Composable
    protected abstract fun compose()

}