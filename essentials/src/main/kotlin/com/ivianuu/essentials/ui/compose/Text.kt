package com.ivianuu.essentials.ui.compose

import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.Ambient
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.ambient
import com.ivianuu.compose.set
import com.ivianuu.compose.setBy

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
        }
    }
}