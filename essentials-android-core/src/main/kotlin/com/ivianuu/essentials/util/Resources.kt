package com.ivianuu.essentials.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.font
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.content.res.ResourcesCompat
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationContext

@Binding
class Resources(
    private val applicationContext: ApplicationContext,
) {

    fun getBitmap(id: Int): ImageAsset = applicationContext.getDrawable(id)!!.toImageAsset()

    fun getBoolean(id: Int): Boolean = applicationContext.resources.getBoolean(id)

    fun getColor(id: Int): Color = Color(applicationContext.getColor(id))

    fun getDimension(id: Int): Dp = with(Density(applicationContext)) {
        applicationContext.resources.getDimension(id).toInt().toDp()
    }

    fun getDrawable(id: Int): ImageAsset = getBitmap(id)

    fun getFloat(id: Int): Float = ResourcesCompat.getFloat(applicationContext.resources, id)

    fun getFont(id: Int): Font = font(id)

    fun getInt(id: Int): Int = applicationContext.resources.getInteger(id)

    fun getIntArray(id: Int): IntArray = applicationContext.resources.getIntArray(id)

    fun getString(id: Int): String = applicationContext.getString(id)

    fun getString(id: Int, vararg args: Any?): String =
        applicationContext.getString(id, *args)

    fun getStringArray(id: Int): Array<String> =
        applicationContext.resources.getStringArray(id)
}
