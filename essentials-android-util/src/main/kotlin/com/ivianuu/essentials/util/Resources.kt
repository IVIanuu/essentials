/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.font
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.content.res.ResourcesCompat
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext

@FunBinding
fun bitmapResource(applicationContext: ApplicationContext, @FunApi id: Int): ImageBitmap =
    applicationContext.getDrawable(id)!!.toImageBitmap()

@FunBinding
fun booleanResource(applicationContext: ApplicationContext, @FunApi id: Int): Boolean =
    applicationContext.resources.getBoolean(id)

@FunBinding
fun colorResource(applicationContext: ApplicationContext, @FunApi id: Int): Color =
    Color(applicationContext.getColor(id))

@FunBinding
fun dimensionResource(applicationContext: ApplicationContext, @FunApi id: Int): Dp =
    with(Density(applicationContext)) {
        applicationContext.resources.getDimension(id).toInt().toDp()
    }

@FunBinding
fun drawableResource(bitmapResource: bitmapResource, @FunApi id: Int): ImageBitmap =
    bitmapResource(id)

@FunBinding
fun floatResource(applicationContext: ApplicationContext, @FunApi id: Int): Float =
    ResourcesCompat.getFloat(applicationContext.resources, id)

@FunBinding
fun fontResource(applicationContext: ApplicationContext, @FunApi id: Int): Font =
    font(id)

@FunBinding
fun intResource(applicationContext: ApplicationContext, @FunApi id: Int): Int =
    applicationContext.resources.getInteger(id)

@FunBinding
fun intArrayResource(applicationContext: ApplicationContext, @FunApi id: Int): IntArray =
    applicationContext.resources.getIntArray(id)

@FunBinding
fun stringResource(applicationContext: ApplicationContext, @FunApi id: Int): String =
    applicationContext.getString(id)

@FunBinding
fun stringResourceWithArguments(
    applicationContext: ApplicationContext,
    @FunApi id: Int,
    @FunApi args: List<Any?>
): String = applicationContext.getString(id, *args.toTypedArray())

@FunBinding
fun stringArrayResource(applicationContext: ApplicationContext, @FunApi id: Int): Array<String> =
    applicationContext.resources.getStringArray(id)
