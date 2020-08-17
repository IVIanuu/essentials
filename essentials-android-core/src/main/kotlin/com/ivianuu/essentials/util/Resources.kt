package com.ivianuu.essentials.util

import androidx.core.content.res.ResourcesCompat
import androidx.ui.graphics.Color
import androidx.ui.graphics.ImageAsset
import androidx.ui.text.font.Font
import androidx.ui.text.font.font
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.injekt.Reader

object Resources {

    @Reader
    fun getBitmap(id: Int): ImageAsset = androidApplicationContext.getDrawable(id)!!.toImageAsset()

    @Reader
    fun getBoolean(id: Int): Boolean = androidApplicationContext.resources.getBoolean(id)

    @Reader
    fun getColor(id: Int): Color = Color(androidApplicationContext.getColor(id))

    @Reader
    fun getDimension(id: Int): Dp = with(Density(androidApplicationContext)) {
        androidApplicationContext.resources.getDimension(id).toInt().toDp()
    }

    @Reader
    fun getDrawable(id: Int): ImageAsset = getBitmap(id)

    @Reader
    fun getFloat(id: Int): Float = ResourcesCompat.getFloat(androidApplicationContext.resources, id)

    @Reader
    fun getFont(id: Int): Font = font(id)

    @Reader
    fun getInt(id: Int): Int = androidApplicationContext.resources.getInteger(id)

    @Reader
    fun getIntArray(id: Int): IntArray = androidApplicationContext.resources.getIntArray(id)

    @Reader
    fun getString(id: Int): String = androidApplicationContext.getString(id)

    @Reader
    fun getString(id: Int, vararg args: Any?): String =
        androidApplicationContext.getString(id, *args)

    @Reader
    fun getStringArray(id: Int): Array<String> =
        androidApplicationContext.resources.getStringArray(id)
}
