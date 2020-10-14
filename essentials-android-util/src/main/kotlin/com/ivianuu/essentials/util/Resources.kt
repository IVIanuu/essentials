package com.ivianuu.essentials.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.font
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.content.res.ResourcesCompat
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext

@FunBinding
fun bitmapResource(applicationContext: ApplicationContext, id: @Assisted Int): ImageAsset =
    applicationContext.getDrawable(id)!!.toImageAsset()

@FunBinding
fun booleanResource(applicationContext: ApplicationContext, id: @Assisted Int): Boolean =
    applicationContext.resources.getBoolean(id)

@FunBinding
fun colorResource(applicationContext: ApplicationContext, id: @Assisted Int): Color =
    Color(applicationContext.getColor(id))

@FunBinding
fun dimensionResource(applicationContext: ApplicationContext, id: @Assisted Int): Dp =
    with(Density(applicationContext)) {
        applicationContext.resources.getDimension(id).toInt().toDp()
    }

@FunBinding
fun drawableResource(bitmapResource: bitmapResource, id: @Assisted Int): ImageAsset =
    bitmapResource(id)

@FunBinding
fun floatResource(applicationContext: ApplicationContext, id: @Assisted Int): Float =
    ResourcesCompat.getFloat(applicationContext.resources, id)

@FunBinding
fun fontResource(applicationContext: ApplicationContext, id: @Assisted Int): Font =
    font(id)

@FunBinding
fun intResource(applicationContext: ApplicationContext, id: @Assisted Int): Int =
    applicationContext.resources.getInteger(id)

@FunBinding
fun intArrayResource(applicationContext: ApplicationContext, id: @Assisted Int): IntArray =
    applicationContext.resources.getIntArray(id)

@FunBinding
fun stringResource(applicationContext: ApplicationContext, id: @Assisted Int): String =
    applicationContext.getString(id)

@FunBinding
fun stringResourceWithArguments(
    applicationContext: ApplicationContext,
    id: @Assisted Int,
    args: @Assisted List<Any?>,
): String =
    applicationContext.getString(id, *args.toTypedArray())

@FunBinding
fun stringArrayResource(applicationContext: ApplicationContext, id: @Assisted Int): Array<String> =
    applicationContext.resources.getStringArray(id)
