@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.util.ContextAware

@PublishedApi
internal val typedValue = TypedValue()

inline fun Context.anim(resId: Int): Animation = AnimationUtils.loadAnimation(this, resId)

inline fun Context.intArray(resId: Int): IntArray = resources.getIntArray(resId)

inline fun Context.stringArray(resId: Int): Array<String> =
    resources.getStringArray(resId)

inline fun Context.textArray(resId: Int): Array<CharSequence> =
    resources.getTextArray(resId)

inline fun Context.typedArray(resId: Int): TypedArray = resources.obtainTypedArray(resId)

inline fun Context.bool(resId: Int): Boolean = resources.getBoolean(resId)

inline fun Context.dimen(resId: Int): Float = resources.getDimension(resId)

inline fun Context.dimenPx(resId: Int): Int = resources.getDimensionPixelSize(resId)

inline fun Context.dimenPxOffset(resId: Int): Int = resources.getDimensionPixelOffset(resId)

inline fun Context.float(resId: Int): Float {
    resources.getValue(resId, typedValue, true)
    return typedValue.float
}

inline fun Context.int(resId: Int): Int = resources.getInteger(resId)

inline fun Context.bitmap(resId: Int): Bitmap = BitmapFactory.decodeResource(resources, resId)

inline fun Context.color(resId: Int): Int = ContextCompat.getColor(this, resId)

inline fun Context.colorStateList(resId: Int): ColorStateList =
    ContextCompat.getColorStateList(this, resId)!!

inline fun Context.drawable(resId: Int): Drawable =
    ContextCompat.getDrawable(this, resId)!!

inline fun Context.font(resId: Int): Typeface = ResourcesCompat.getFont(this, resId)!!

inline fun Context.string(resId: Int): String = getString(resId)

inline fun Context.string(resId: Int, vararg args: Any): String = getString(resId, *args)

inline fun ContextAware.anim(resId: Int) = providedContext.anim(resId)

inline fun ContextAware.intArray(resId: Int) = providedContext.intArray(resId)

inline fun ContextAware.stringArray(resId: Int) = providedContext.stringArray(resId)

inline fun ContextAware.textArray(resId: Int) = providedContext.textArray(resId)

inline fun ContextAware.typedArray(resId: Int) = providedContext.typedArray(resId)

inline fun ContextAware.bool(resId: Int) = providedContext.bool(resId)

inline fun ContextAware.dimen(resId: Int) = providedContext.dimen(resId)

inline fun ContextAware.dimenPx(resId: Int) = providedContext.dimenPx(resId)

inline fun ContextAware.dimenPxOffset(resId: Int) = providedContext.dimenPxOffset(resId)

inline fun ContextAware.float(resId: Int) = providedContext.float(resId)

inline fun ContextAware.int(resId: Int): Int = providedContext.int(resId)

inline fun ContextAware.bitmap(resId: Int) = providedContext.bitmap(resId)

inline fun ContextAware.color(resId: Int) = providedContext.color(resId)

inline fun ContextAware.colorStateList(resId: Int) =
    providedContext.colorStateList(resId)

inline fun ContextAware.drawable(resId: Int) = providedContext.drawable(resId)

inline fun ContextAware.font(resId: Int) = providedContext.font(resId)

inline fun ContextAware.string(resId: Int) = providedContext.string(resId)

inline fun ContextAware.string(resId: Int, vararg args: Any) =
    providedContext.string(resId, *args)

inline fun Fragment.anim(resId: Int) = requireContext().anim(resId)

inline fun Fragment.intArray(resId: Int) = requireContext().intArray(resId)

inline fun Fragment.stringArray(resId: Int) = requireContext().stringArray(resId)

inline fun Fragment.textArray(resId: Int) = requireContext().textArray(resId)

inline fun Fragment.typedArray(resId: Int) = requireContext().typedArray(resId)

inline fun Fragment.bool(resId: Int) = requireContext().bool(resId)

inline fun Fragment.dimen(resId: Int) = requireContext().dimen(resId)

inline fun Fragment.dimenPx(resId: Int) = requireContext().dimenPx(resId)

inline fun Fragment.dimenPxOffset(resId: Int) = requireContext().dimenPxOffset(resId)

inline fun Fragment.float(resId: Int) = requireContext().float(resId)

inline fun Fragment.int(resId: Int): Int = requireContext().int(resId)

inline fun Fragment.bitmap(resId: Int) = requireContext().bitmap(resId)

inline fun Fragment.color(resId: Int) = requireContext().color(resId)

inline fun Fragment.colorStateList(resId: Int) =
    requireContext().colorStateList(resId)

inline fun Fragment.drawable(resId: Int) = requireContext().drawable(resId)

inline fun Fragment.font(resId: Int) = requireContext().font(resId)

inline fun Fragment.string(resId: Int) = requireContext().string(resId)

inline fun Fragment.string(resId: Int, vararg args: Any) =
    requireContext().string(resId, *args)

inline fun View.anim(resId: Int) = context.anim(resId)

inline fun View.intArray(resId: Int) = context.intArray(resId)

inline fun View.stringArray(resId: Int) = context.stringArray(resId)

inline fun View.textArray(resId: Int) = context.textArray(resId)

inline fun View.typedArray(resId: Int) = context.typedArray(resId)

inline fun View.bool(resId: Int) = context.bool(resId)

inline fun View.dimen(resId: Int) = context.dimen(resId)

inline fun View.dimenPx(resId: Int) = context.dimenPx(resId)

inline fun View.dimenPxOffset(resId: Int) = context.dimenPxOffset(resId)

inline fun View.float(resId: Int) = context.float(resId)

inline fun View.int(resId: Int): Int = context.int(resId)

inline fun View.bitmap(resId: Int) = context.bitmap(resId)

inline fun View.color(resId: Int) = context.color(resId)

inline fun View.colorStateList(resId: Int) =
    context.colorStateList(resId)

inline fun View.drawable(resId: Int) = context.drawable(resId)

inline fun View.font(resId: Int) = context.font(resId)

inline fun View.string(resId: Int) = context.string(resId)

inline fun View.string(resId: Int, vararg args: Any) =
    context.string(resId, *args)