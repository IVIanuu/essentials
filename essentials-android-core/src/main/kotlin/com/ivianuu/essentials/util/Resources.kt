package com.ivianuu.essentials.util

import androidx.core.content.res.ResourcesCompat
import androidx.ui.graphics.Color
import androidx.ui.graphics.ImageAsset
import androidx.ui.text.font.Font
import androidx.ui.text.font.font
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.injekt.Reader

object Resources {

    @Reader
    fun getBitmap(id: Int): ImageAsset = applicationContext.getDrawable(id)!!.toImageAsset()

    @Reader
    fun getBoolean(id: Int): Boolean = applicationContext.resources.getBoolean(id)

    @Reader
    fun getColor(id: Int): Color = Color(applicationContext.getColor(id))

    @Reader
    fun getDimension(id: Int): Dp = with(Density(applicationContext)) {
        applicationContext.resources.getDimension(id).toInt().toDp()
    }

    @Reader
    fun getDrawable(id: Int): ImageAsset = getBitmap(id)

    @Reader
    fun getFloat(id: Int): Float = ResourcesCompat.getFloat(applicationContext.resources, id)

    @Reader
    fun getFont(id: Int): Font = font(id)

    @Reader
    fun getInt(id: Int): Int = applicationContext.resources.getInteger(id)

    @Reader
    fun getIntArray(id: Int): IntArray = applicationContext.resources.getIntArray(id)

    @Reader
    fun getString(id: Int): String = applicationContext.getString(id)

    @Reader
    fun getString(id: Int, vararg args: Any?): String = applicationContext.getString(id, *args)

    @Reader
    fun getStringArray(id: Int): Array<String> =
        applicationContext.resources.getStringArray(id)
}
