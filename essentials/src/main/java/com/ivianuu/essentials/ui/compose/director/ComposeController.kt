package com.ivianuu.essentials.ui.compose.director

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.compose.core.WidgetComposition
import com.ivianuu.essentials.ui.compose.core.disposeComposition
import com.ivianuu.essentials.ui.compose.core.setViewContent
import com.ivianuu.essentials.util.cast

abstract class ComposeController : EsController() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return FrameLayout(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }.also { setContentView(it) }
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        view.cast<ViewGroup>().setViewContent { build() }
    }

    override fun onDestroyView(view: View) {
        view.cast<ViewGroup>().disposeComposition()
        super.onDestroyView(view)
    }

    protected abstract fun WidgetComposition.build()

}