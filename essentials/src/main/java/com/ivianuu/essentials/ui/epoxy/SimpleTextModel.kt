package com.ivianuu.essentials.ui.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.essentials.R2
import kotlinx.android.synthetic.main.es_item_simple_text.*

/**
 * Simple text model
 */
@EpoxyModelClass(layout = R2.layout.es_item_simple_text)
abstract class SimpleTextModel : SimpleEpoxyModel() {

    @EpoxyAttribute var text = ""
    @EpoxyAttribute var textRes = 0

    override fun bind(holder: EsEpoxyHolder) {
        super.bind(holder)

        when {
            text.isNotEmpty() -> holder.es_text.text = text
            textRes != 0 -> holder.es_text.setText(textRes)
            else -> error("you must specify one of text or textRes")
        }
    }

}