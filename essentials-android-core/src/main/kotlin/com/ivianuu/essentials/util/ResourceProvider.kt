package com.ivianuu.essentials.util

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.ui.graphics.Color
import androidx.ui.graphics.ImageAsset
import androidx.ui.text.font.Font
import androidx.ui.text.font.font
import androidx.ui.unit.Dp
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

@Transient
class ResourceProvider(
    private val context: @ForApplication Context,
    private val densityProvider: DensityProvider
) {

    fun getBitmap(id: Int): ImageAsset {
        return context.getDrawable(id)!!.toImageAsset()
    }

    fun getBoolean(id: Int): Boolean = context.resources.getBoolean(id)

    fun getColor(id: Int): Color = Color(context.getColor(id))

    fun getDimension(id: Int): Dp = densityProvider.withDensity {
        context.resources.getDimension(id).toInt().toDp()
    }

    fun getDrawable(id: Int): ImageAsset = getBitmap(id)

    fun getFloat(id: Int): Float = ResourcesCompat.getFloat(context.resources, id)

    fun getFont(id: Int): Font = font(id)

    fun getInt(id: Int): Int = context.resources.getInteger(id)

    fun getIntArray(id: Int): IntArray = context.resources.getIntArray(id)

    fun getString(id: Int): String = context.getString(id)

    fun getString(id: Int, vararg args: Any?): String = context.getString(id, *args)

    fun getStringArray(id: Int): Array<String> =
        context.resources.getStringArray(id)
}