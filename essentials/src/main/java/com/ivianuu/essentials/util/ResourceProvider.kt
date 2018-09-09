package com.ivianuu.essentials.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import javax.inject.Inject

/**
 * Resource provider
 */
class ResourceProvider @Inject constructor(
    private val context: Context,
    private val resources: Resources
) {

    private val typedValue = TypedValue()

    fun anim(resId: Int): Animation = AnimationUtils.loadAnimation(context, resId)

    fun intArray(resId: Int): IntArray = resources.getIntArray(resId)

    fun stringArray(resId: Int): Array<String> =
        resources.getStringArray(resId)

    fun textArray(resId: Int): Array<CharSequence> =
        resources.getTextArray(resId)

    fun typedArray(resId: Int): TypedArray = resources.obtainTypedArray(resId)

    fun bool(resId: Int): Boolean = resources.getBoolean(resId)

    fun dimen(resId: Int): Float = resources.getDimension(resId)

    fun dimenPx(resId: Int): Int = resources.getDimensionPixelSize(resId)

    fun dimenPxOffset(resId: Int): Int = resources.getDimensionPixelOffset(resId)

    fun float(resId: Int): Float {
        resources.getValue(resId, typedValue, true)
        return typedValue.float
    }

    fun int(resId: Int): Int = resources.getInteger(resId)

    fun bitmap(resId: Int): Bitmap = BitmapFactory.decodeResource(resources, resId)

    fun color(resId: Int): Int = ContextCompat.getColor(context, resId)

    fun colorStateList(resId: Int): ColorStateList =
        ContextCompat.getColorStateList(context, resId)!!

    fun drawable(resId: Int): Drawable =
        ContextCompat.getDrawable(context, resId)!!

    fun font(resId: Int): Typeface = ResourcesCompat.getFont(context, resId)!!

    fun string(resId: Int): String = context.getString(resId)

    fun string(resId: Int, vararg args: Any): String = context.getString(resId, *args)
}