package com.ivianuu.essentials.util

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.use
import javax.inject.Inject

/**
 * Provide attributes
 */
class AttributeProvider @Inject constructor(private val context: Context) {

    fun boolean(attr: Int, defaultValue: Boolean = false): Boolean =
        withTypedArray(attr) { it.getBoolean(0, defaultValue) }

    fun color(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { it.getColor(0, defaultValue) }

    fun colorStateList(
        attr: Int,
        defaultValue: ColorStateList? = null
    ): ColorStateList? = withTypedArray(attr) { it.getColorStateList(0) ?: defaultValue }

    fun dimen(attr: Int, defaultValue: Float = 0f): Float =
        withTypedArray(attr) { it.getDimension(0, defaultValue) }

    fun dimenPxOffset(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { it.getDimensionPixelOffset(0, defaultValue) }

    fun dimenPx(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { it.getDimensionPixelSize(0, defaultValue) }

    fun drawable(attr: Int, defaultValue: Drawable? = null): Drawable? =
        withTypedArray(attr) { it.getDrawable(0) ?: defaultValue }

    fun float(attr: Int, defaultValue: Float = 0f): Float =
        withTypedArray(attr) { it.getFloat(0, defaultValue) }

    @TargetApi(Build.VERSION_CODES.O)
    fun font(attr: Int, defaultValue: Typeface? = null): Typeface? =
        withTypedArray(attr) { it.getFont(0) ?: defaultValue }

    fun intArray(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { it.getInt(0, defaultValue) }

    fun integer(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { it.getInteger(0, defaultValue) }

    fun string(attr: Int, defaultValue: String? = null): String? =
        withTypedArray(attr) { it.getString(0) ?: defaultValue }

    fun text(attr: Int, defaultValue: CharSequence? = null): CharSequence? =
        withTypedArray(attr) { it.getText(0) ?: defaultValue }

    fun textArray(
        attr: Int,
        defaultValue: Array<CharSequence>? = null
    ): Array<CharSequence>? = withTypedArray(attr) { it.getTextArray(0) ?: defaultValue }

    private inline fun <T> withTypedArray(
        vararg attr: Int,
        block: (TypedArray) -> T
    ): T = context.theme.obtainStyledAttributes(attr).use(block)
}