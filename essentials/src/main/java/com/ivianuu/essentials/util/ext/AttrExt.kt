@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.util.ContextAware

inline fun Context.booleanAttr(attr: Int, defaultValue: Boolean = false): Boolean =
    withTypedArray(attr) { it.getBoolean(0, defaultValue) }

inline fun Context.colorAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getColor(0, defaultValue) }

inline fun Context.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
): ColorStateList? = withTypedArray(attr) { it.getColorStateList(0) ?: defaultValue }

inline fun Context.dimenAttr(attr: Int, defaultValue: Float = 0f): Float =
    withTypedArray(attr) { it.getDimension(0, defaultValue) }

inline fun Context.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getDimensionPixelOffset(0, defaultValue) }

inline fun Context.dimenPxAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getDimensionPixelSize(0, defaultValue) }

inline fun Context.drawableAttr(attr: Int, defaultValue: Drawable? = null): Drawable? =
    withTypedArray(attr) { it.getDrawable(0) ?: defaultValue }

inline fun Context.floatAttr(attr: Int, defaultValue: Float = 0f): Float =
    withTypedArray(attr) { it.getFloat(0, defaultValue) }

@TargetApi(Build.VERSION_CODES.O)
inline fun Context.fontAttr(attr: Int, defaultValue: Typeface? = null): Typeface? =
    withTypedArray(attr) { it.getFont(0) ?: defaultValue }

inline fun Context.intArrayAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getInt(0, defaultValue) }

inline fun Context.integerAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getInteger(0, defaultValue) }

inline fun Context.stringAttr(attr: Int, defaultValue: String? = null): String? =
    withTypedArray(attr) { it.getString(0) ?: defaultValue }

inline fun Context.textAttr(attr: Int, defaultValue: CharSequence? = null): CharSequence? =
    withTypedArray(attr) { it.getText(0) ?: defaultValue }

inline fun Context.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
): Array<CharSequence>? = withTypedArray(attr) { it.getTextArray(0) ?: defaultValue }

inline fun ContextAware.booleanAttr(attr: Int, defaultValue: Boolean = false) =
    providedContext.booleanAttr(attr, defaultValue)

inline fun ContextAware.colorAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.colorAttr(attr, defaultValue)

inline fun ContextAware.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
) =
    providedContext.colorStateListAttr(attr, defaultValue)

inline fun ContextAware.dimenAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.dimenAttr(attr, defaultValue)

inline fun ContextAware.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.dimenPxOffsetAttr(attr, defaultValue)

inline fun ContextAware.dimenPxAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.dimenPxAttr(attr, defaultValue)

inline fun ContextAware.drawableAttr(attr: Int, defaultValue: Drawable? = null) =
    providedContext.drawableAttr(attr, defaultValue)

inline fun ContextAware.floatAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.floatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
inline fun ContextAware.fontAttr(attr: Int, defaultValue: Typeface? = null) =
    providedContext.fontAttr(attr, defaultValue)

inline fun ContextAware.intArrayAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.intArrayAttr(attr, defaultValue)

inline fun ContextAware.integerAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.integerAttr(attr, defaultValue)

inline fun ContextAware.stringAttr(attr: Int, defaultValue: String? = null) =
    providedContext.stringAttr(attr, defaultValue)

inline fun ContextAware.textAttr(attr: Int, defaultValue: CharSequence? = null) =
    providedContext.textAttr(attr, defaultValue)

inline fun ContextAware.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
) =
    providedContext.textArrayAttr(attr, defaultValue)

inline fun Fragment.booleanAttr(attr: Int, defaultValue: Boolean = false) =
    requireContext().booleanAttr(attr, defaultValue)

inline fun Fragment.colorAttr(attr: Int, defaultValue: Int = 0) =
    requireContext().colorAttr(attr, defaultValue)

inline fun Fragment.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
) =
    requireContext().colorStateListAttr(attr, defaultValue)

inline fun Fragment.dimenAttr(attr: Int, defaultValue: Float = 0f) =
    requireContext().dimenAttr(attr, defaultValue)

inline fun Fragment.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    requireContext().dimenPxOffsetAttr(attr, defaultValue)

inline fun Fragment.dimenPxAttr(attr: Int, defaultValue: Int = 0) =
    requireContext().dimenPxAttr(attr, defaultValue)

inline fun Fragment.drawableAttr(attr: Int, defaultValue: Drawable? = null) =
    requireContext().drawableAttr(attr, defaultValue)

inline fun Fragment.floatAttr(attr: Int, defaultValue: Float = 0f) =
    requireContext().floatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
inline fun Fragment.fontAttr(attr: Int, defaultValue: Typeface? = null) =
    requireContext().fontAttr(attr, defaultValue)

inline fun Fragment.intArrayAttr(attr: Int, defaultValue: Int = 0) =
    requireContext().intArrayAttr(attr, defaultValue)

inline fun Fragment.integerAttr(attr: Int, defaultValue: Int = 0) =
    requireContext().integerAttr(attr, defaultValue)

inline fun Fragment.stringAttr(attr: Int, defaultValue: String? = null) =
    requireContext().stringAttr(attr, defaultValue)

inline fun Fragment.textAttr(attr: Int, defaultValue: CharSequence? = null) =
    requireContext().textAttr(attr, defaultValue)

inline fun Fragment.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
) =
    requireContext().textArrayAttr(attr, defaultValue)

inline fun View.booleanAttr(attr: Int, defaultValue: Boolean = false) =
    context.booleanAttr(attr, defaultValue)

inline fun View.colorAttr(attr: Int, defaultValue: Int = 0) =
    context.colorAttr(attr, defaultValue)

inline fun View.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
) =
    context.colorStateListAttr(attr, defaultValue)

inline fun View.dimenAttr(attr: Int, defaultValue: Float = 0f) =
    context.dimenAttr(attr, defaultValue)

inline fun View.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    context.dimenPxOffsetAttr(attr, defaultValue)

inline fun View.dimenPxAttr(attr: Int, defaultValue: Int = 0) =
    context.dimenPxAttr(attr, defaultValue)

inline fun View.drawableAttr(attr: Int, defaultValue: Drawable? = null) =
    context.drawableAttr(attr, defaultValue)

inline fun View.floatAttr(attr: Int, defaultValue: Float = 0f) =
    context.floatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
inline fun View.fontAttr(attr: Int, defaultValue: Typeface? = null) =
    context.fontAttr(attr, defaultValue)

inline fun View.intArrayAttr(attr: Int, defaultValue: Int = 0) =
    context.intArrayAttr(attr, defaultValue)

inline fun View.integerAttr(attr: Int, defaultValue: Int = 0) =
    context.integerAttr(attr, defaultValue)

inline fun View.stringAttr(attr: Int, defaultValue: String? = null) =
    context.stringAttr(attr, defaultValue)

inline fun View.textAttr(attr: Int, defaultValue: CharSequence? = null) =
    context.textAttr(attr, defaultValue)

inline fun View.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
) =
    context.textArrayAttr(attr, defaultValue)

@PublishedApi
internal inline fun <T> Context.withTypedArray(
    vararg attr: Int,
    block: (TypedArray) -> T
): T =
    theme.obtainStyledAttributes(attr).use(block)
