package com.ivianuu.essentials.ui.compose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient
import com.ivianuu.essentials.ui.compose.core.ControllerAmbient
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.injekt.MaterialThemeProvider
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.cast

/**
 * Controller which uses compose to display it's ui
 */
abstract class ComposeController : EsController() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup) =
        FrameLayout(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            onViewCreated(this)
        }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        view.cast<ViewGroup>().setContent { composeWithAmbients() }
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        // todo use disposeComposition once fixed
        view.cast<ViewGroup>().setContent { }
    }

    @Composable
    protected open fun composeWithAmbients() {
        val materialThemeProvider = +inject<MaterialThemeProvider>()
        MaterialTheme(
            colors = +materialThemeProvider.colors,
            typography = +materialThemeProvider.typography
        ) {
            ActivityAmbient.Provider(value = requireActivity()) {
                RouteAmbient.Provider(value = route!!) {
                    ControllerAmbient.Provider(value = this) {
                        ComponentAmbient.Provider(value = component) {
                            compose()
                        }
                    }
                }
            }
        }
    }

    @Composable
    protected abstract fun compose()
}