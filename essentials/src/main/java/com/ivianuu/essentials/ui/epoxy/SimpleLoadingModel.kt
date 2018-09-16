package com.ivianuu.essentials.ui.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.androidktx.core.widget.indeterminateColor
import com.ivianuu.androidktx.core.widget.progressColor
import com.ivianuu.androidktx.core.widget.secondaryProgressColor
import com.ivianuu.essentials.R2
import com.ivianuu.essentials.util.color
import kotlinx.android.synthetic.main.item_simple_loading.*

/**
 * Simple loading model
 */
@EpoxyModelClass(layout = R2.layout.item_simple_loading)
abstract class SimpleLoadingModel : SimpleEpoxyModel() {

    @EpoxyAttribute var progressColor = 0
    @EpoxyAttribute var progressColorRes = 0

    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)
        if (progressColor != 0 || progressColorRes != 0) {
            val progressColor = when {
                progressColor != 0 -> progressColor
                progressColorRes != 0 -> color(progressColorRes)
                else -> 0
            }

            progress_bar.progressColor = progressColor
            progress_bar.secondaryProgressColor = progressColor
            progress_bar.indeterminateColor = progressColor
        }
    }

}