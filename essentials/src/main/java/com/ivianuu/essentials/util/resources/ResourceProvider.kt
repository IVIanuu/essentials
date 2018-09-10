package com.ivianuu.essentials.util.resources

import android.content.Context
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.animation.AnimationUtils
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import javax.inject.Inject

/**
 * Resource provider
 */
class ResourceProvider @Inject constructor(private val baseContext: Context) {

    private var appliedTheme = ResourcesPlugins.themeResId
    private var themedContext = baseContext

    private val typedValue = TypedValue()

    fun anim(resId: Int) = withContext { AnimationUtils.loadAnimation(themedContext, resId) }

    fun intArray(resId: Int) = withContext { resources.getIntArray(resId) }

    fun stringArray(resId: Int) = withContext { resources.getStringArray(resId) }

    fun textArray(resId: Int) = withContext { resources.getTextArray(resId) }

    fun typedArray(resId: Int) = withContext { resources.obtainTypedArray(resId) }

    fun bool(resId: Int) = withContext { resources.getBoolean(resId) }

    fun dimen(resId: Int) = withContext { resources.getDimension(resId) }

    fun dimenPx(resId: Int) = withContext { resources.getDimensionPixelSize(resId) }

    fun dimenPxOffset(resId: Int) = withContext { resources.getDimensionPixelOffset(resId) }

    fun float(resId: Int) = withContext {
        resources.getValue(resId, typedValue, true)
        typedValue.float
    }

    fun int(resId: Int) =
        withContext { resources.getInteger(resId) }

    fun bitmap(resId: Int) =
        withContext { BitmapFactory.decodeResource(resources, resId) }

    fun color(resId: Int) = withContext { ContextCompat.getColor(themedContext, resId) }

    fun colorStateList(resId: Int) =
        withContext { ContextCompat.getColorStateList(themedContext, resId)!! }

    fun drawable(resId: Int) =
        withContext { ContextCompat.getDrawable(themedContext, resId)!! }

    fun font(resId: Int) =
        withContext { ResourcesCompat.getFont(themedContext, resId)!! }

    fun string(resId: Int) = withContext { themedContext.getString(resId) }

    fun string(resId: Int, vararg args: Any) =
        withContext { themedContext.getString(resId, *args) }

    private inline fun <T> withContext(block: Context.() -> T): T {
        checkTheme()
        return block(themedContext)
    }

    private fun checkTheme() {
        val themeResId = ResourcesPlugins.themeResId
        if (appliedTheme != themeResId) {
            themedContext = ContextThemeWrapper(baseContext, themeResId)
            appliedTheme = themeResId
        }
    }
}