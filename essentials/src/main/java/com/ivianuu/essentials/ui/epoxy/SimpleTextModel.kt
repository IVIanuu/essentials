package com.ivianuu.essentials.ui.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.androidktx.appcompat.widget.textFuture
import com.ivianuu.androidktx.appcompat.widget.textFutureResource
import com.ivianuu.essentials.R2
import kotlinx.android.synthetic.main.item_simple_text.*

/**
 * Simple text model
 */
@EpoxyModelClass(layout = R2.layout.item_simple_text)
abstract class SimpleTextModel : SimpleEpoxyModel() {

    @EpoxyAttribute var text = ""
    @EpoxyAttribute var textRes = 0

    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)

        when {
            text.isNotEmpty() -> holder.text.textFuture = text
            textRes != 0 -> holder.text.textFutureResource = textRes
            else -> throw IllegalStateException("you must specify one of text or textRes")
        }
    }

}