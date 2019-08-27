package com.ivianuu.essentials.ui.compose

import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.Ambient
import androidx.core.view.updateLayoutParams
import com.github.ajalt.timberkt.d
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.ambient
import com.ivianuu.compose.currentComponent
import com.ivianuu.compose.init
import com.ivianuu.compose.set
import com.ivianuu.compose.setBy
import com.ivianuu.kommon.core.content.string
import com.ivianuu.kommon.core.view.dp

val TextAppearanceAmbient = Ambient.of<Int?>(key = "TextAppearance")
val TextColorAmbient = Ambient.of<Int?>(key = "TextColor")

fun ComponentComposition.TextStyle(
    textAppearance: Int? = null,
    textColor: Int? = null,
    children: ComponentComposition.() -> Unit
) {
    TextAppearanceAmbient.Provider(value = textAppearance) {
        TextColorAmbient.Provider(value = textColor) {
            children()
        }
    }
}

fun ComponentComposition.Text(
    text: String? = null,
    textRes: Int? = null,
    textAppearance: Int? = ambient(TextAppearanceAmbient),
    textColor: Int? = ambient(TextColorAmbient)
) {
    View<AppCompatTextView> {
        set(textAppearance) {
            if (it != null) {
                setTextAppearance(it)
            }
        }

        set(textColor) {
            if (it != null) {
                setTextColor(it)
            }
        }

        setBy(text, textRes) {
            when {
                text != null -> this.text = text
                textRes != null -> setText(textRes)
                else -> this.text = null
            }
            d { "bound text ${this.text}" }
        }
    }
}