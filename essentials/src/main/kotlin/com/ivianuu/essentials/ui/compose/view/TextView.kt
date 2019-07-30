package com.ivianuu.essentials.ui.compose.view

import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.ViewComposition
import androidx.compose.unaryPlus
import androidx.ui.core.currentTextStyle
import androidx.ui.core.text.AndroidFontResourceLoader
import androidx.ui.core.withDensity
import androidx.ui.graphics.Color
import androidx.ui.layout.Alignment
import androidx.ui.text.TextStyle
import androidx.ui.text.font.EsTypefaceAdapter
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontSynthesis
import androidx.ui.text.font.FontWeight
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.toGravityInt
import java.util.*

inline fun ViewComposition.TextView(noinline block: ViewDsl<AppCompatTextView>.() -> Unit) =
    TextView(sourceLocation(), block)

fun ViewComposition.TextView(key: Any, block: ViewDsl<AppCompatTextView>.() -> Unit) =
    View(key, { AppCompatTextView(it) }) {
        textStyle(+currentTextStyle())
        block()
    }

fun <T : TextView> ViewDsl<T>.text(text: String?) {
    set(text) { this.text = it }
}

fun <T : TextView> ViewDsl<T>.textStyle(textStyle: TextStyle) {
    +withDensity {
        set(textStyle) {
            if (it.color != null) setTextColor(it.color!!.toArgb())
            if (it.fontSize != null) setTextSize(TypedValue.COMPLEX_UNIT_SP, it.fontSize!!.value)
            if (it.fontSizeScale != null) textScaleX = it.fontSizeScale!! // todo

            // todo dirty
            val adapter = EsTypefaceAdapter(
                resourceLoader = AndroidFontResourceLoader(node.context)
            )
            typeface = adapter.create(
                it.fontFamily,
                it.fontWeight ?: FontWeight.normal,
                it.fontStyle ?: FontStyle.Normal,
                it.fontSynthesis ?: FontSynthesis.All
            )

            if (it.letterSpacing != null) letterSpacing = it.letterSpacing!!
            // todo baselineShift
            // todo textGeometricTransform
            if (it.locale != null) textLocale =
                Locale(it.locale!!.languageCode, it.locale!!.countryCode)
            // todo decoration
            if (it.shadow != null) setShadowLayer(
                it.shadow!!.blurRadius.value,
                it.shadow!!.offset.dx,
                it.shadow!!.offset.dy,
                it.color!!.toArgb()
            )
        }
    }
}

fun <T : TextView> ViewDsl<T>.textColor(color: Color) {
    set(color) { setTextColor(color.toArgb()) }
}

// todo replace with Compose text style
fun <T : TextView> ViewDsl<T>.textAppearance(textAppearance: Int) {
    set(textAppearance) { setTextAppearance(textAppearance) }
}

fun <T : TextView> ViewDsl<T>.textGravity(gravity: Alignment) {
    set(gravity) { setGravity(gravity.toGravityInt()) }
}

fun <T : TextView> ViewDsl<T>.maxLines(maxLines: Int) {
    set(maxLines) { setMaxLines(maxLines) }
}