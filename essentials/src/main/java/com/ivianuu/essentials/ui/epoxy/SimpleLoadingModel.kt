package com.ivianuu.essentials.ui.epoxy

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.essentials.R2
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
                progressColorRes != 0 -> ContextCompat.getColor(
                    holder.containerView.context,
                    progressColorRes
                )
                else -> 0
            }

            val colorStateList = ColorStateList.valueOf(progressColor)
            with(holder.progress_bar) {
                progressTintList = colorStateList
                secondaryProgressTintList = colorStateList
                indeterminateTintList = colorStateList
            }
        }
    }

}