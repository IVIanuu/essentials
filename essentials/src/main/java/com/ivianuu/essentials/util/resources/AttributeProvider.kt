package com.ivianuu.essentials.util.resources

import android.annotation.SuppressLint
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
        withTypedArray(attr) { getBoolean(0, defaultValue) }

    fun color(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { getColor(0, defaultValue) }

    fun colorStateList(
        attr: Int,
        defaultValue: ColorStateList? = null
    ): ColorStateList? = withTypedArray(attr) { getColorStateList(0) ?: defaultValue }

    fun dimen(attr: Int, defaultValue: Float = 0f): Float =
        withTypedArray(attr) { getDimension(0, defaultValue) }

    fun dimenPxOffset(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { getDimensionPixelOffset(0, defaultValue) }

    fun dimenPx(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { getDimensionPixelSize(0, defaultValue) }

    fun drawable(attr: Int, defaultValue: Drawable? = null): Drawable? =
        withTypedArray(attr) { getDrawable(0) ?: defaultValue }

    fun float(attr: Int, defaultValue: Float = 0f): Float =
        withTypedArray(attr) { getFloat(0, defaultValue) }

    @TargetApi(Build.VERSION_CODES.O)
    fun font(attr: Int, defaultValue: Typeface? = null): Typeface? =
        withTypedArray(attr) { getFont(0) ?: defaultValue }

    fun intArray(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { getInt(0, defaultValue) }

    fun integer(attr: Int, defaultValue: Int = 0): Int =
        withTypedArray(attr) { getInteger(0, defaultValue) }

    fun string(attr: Int, defaultValue: String? = null): String? =
        withTypedArray(attr) { getString(0) ?: defaultValue }

    fun text(attr: Int, defaultValue: CharSequence? = null): CharSequence? =
        withTypedArray(attr) { getText(0) ?: defaultValue }

    fun textArray(
        attr: Int,
        defaultValue: Array<CharSequence>? = null
    ): Array<CharSequence>? = withTypedArray(attr) { getTextArray(0) ?: defaultValue }

    @SuppressLint("Recycle")
    private inline fun <T> withTypedArray(
        vararg attr: Int,
        block: TypedArray.() -> T
    ): T = context.obtainStyledAttributes(attr).use(block)
}