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
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.ApplicationContext

@GivenFun
fun bitmapResource(id: Int, @Given applicationContext: ApplicationContext): ImageBitmap =
    applicationContext.getDrawable(id)!!.toImageBitmap()

@GivenFun
fun booleanResource(id: Int, @Given applicationContext: ApplicationContext): Boolean =
    applicationContext.resources.getBoolean(id)

@GivenFun
fun colorResource(id: Int, @Given applicationContext: ApplicationContext): Color =
    Color(applicationContext.getColor(id))

@GivenFun
fun dimensionResource(id: Int, @Given applicationContext: ApplicationContext): Dp =
    with(Density(applicationContext)) {
        applicationContext.resources.getDimension(id).toInt().toDp()
    }

@GivenFun
fun drawableResource(id: Int, @Given bitmapResource: bitmapResource): ImageBitmap =
    bitmapResource(id)

@GivenFun
fun floatResource(id: Int, @Given applicationContext: ApplicationContext): Float =
    ResourcesCompat.getFloat(applicationContext.resources, id)

@GivenFun
fun fontResource(id: Int, @Given applicationContext: ApplicationContext): Font =
    font(id)

@GivenFun
fun intResource(id: Int, @Given applicationContext: ApplicationContext): Int =
    applicationContext.resources.getInteger(id)

@GivenFun
fun intArrayResource(id: Int, @Given applicationContext: ApplicationContext): IntArray =
    applicationContext.resources.getIntArray(id)

@GivenFun
fun stringResource(id: Int, @Given applicationContext: ApplicationContext): String =
    applicationContext.getString(id)

@GivenFun
fun stringResourceWithArguments(
    id: Int,
    args: List<Any?>,
    @Given applicationContext: ApplicationContext
): String = applicationContext.getString(id, *args.toTypedArray())

@GivenFun
fun stringArrayResource(id: Int, @Given applicationContext: ApplicationContext): Array<String> =
    applicationContext.resources.getStringArray(id)
