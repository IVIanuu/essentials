package com.ivianuu.essentials.util

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.unit.Dp
import androidx.ui.unit.px
import com.ivianuu.essentials.ui.image.toImage
import com.ivianuu.injekt.Factory

@Factory
class ResourceProvider(
    private val context: Context,
    private val densityProvider: DensityProvider
) {

    fun getBitmap(id: Int): Image = BitmapFactory.decodeResource(context.resources, id).toImage()

    fun getBoolean(id: Int): Boolean = context.resources.getBoolean(id)

    fun getColor(id: Int): Color = Color(context.getColor(id))

    fun getDimension(id: Int): Dp = densityProvider.withDensity {
        context.resources.getDimension(id).px.toDp()
    }

    fun getDrawable(id: Int): Image = ContextCompat.getDrawable(context, id)!!.toImage()

    fun getFloat(id: Int): Float = ResourcesCompat.getFloat(context.resources, id)

    // todo fun Context.getFont(resId: Int): Typeface = ResourcesCompat.getFont(this, resId)!!

    fun getInt(id: Int): Int = context.resources.getInteger(id)

    fun getIntArray(id: Int): IntArray = context.resources.getIntArray(id)

    fun getString(id: Int): String = context.getString(id)

    fun getString(id: Int, vararg args: Any?): String = context.getString(id, *args)

    fun getStringArray(id: Int): Array<String> =
        context.resources.getStringArray(id)

}
