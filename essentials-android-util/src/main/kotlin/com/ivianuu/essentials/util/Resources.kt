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
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.font
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.content.res.ResourcesCompat
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext

typealias bitmapResource = (Int) -> ImageAsset
@FunBinding
fun bitmapResource(applicationContext: ApplicationContext, id: Int): ImageAsset =
    applicationContext.getDrawable(id)!!.toImageAsset()

typealias booleanResource = (Int) -> Boolean
@FunBinding
fun booleanResource(applicationContext: ApplicationContext, id: Int): Boolean =
    applicationContext.resources.getBoolean(id)

typealias colorResource = (Int) -> Color
@FunBinding
fun colorResource(applicationContext: ApplicationContext, id: Int): Color =
    Color(applicationContext.getColor(id))

typealias dimensionResource = (Int) -> Dp
@FunBinding
fun dimensionResource(applicationContext: ApplicationContext, id: Int): Dp =
    with(Density(applicationContext)) {
        applicationContext.resources.getDimension(id).toInt().toDp()
    }

typealias drawableResource = (Int) -> ImageAsset
@FunBinding
fun drawableResource(bitmapResource: bitmapResource, id: Int): ImageAsset =
    bitmapResource(id)

typealias floatResource = (Int) -> Float
@FunBinding
fun floatResource(applicationContext: ApplicationContext, id: Int): Float =
    ResourcesCompat.getFloat(applicationContext.resources, id)

typealias fontResource = (Int) -> Font
@FunBinding
fun fontResource(applicationContext: ApplicationContext, id: Int): Font =
    font(id)

typealias intResource = (Int) -> Int
@FunBinding
fun intResource(applicationContext: ApplicationContext, id: Int): Int =
    applicationContext.resources.getInteger(id)

typealias intArrayResource = (Int) -> IntArray
@FunBinding
fun intArrayResource(applicationContext: ApplicationContext, id: Int): IntArray =
    applicationContext.resources.getIntArray(id)

typealias stringResource = (Int) -> String
@FunBinding
fun stringResource(applicationContext: ApplicationContext, id: Int): String =
    applicationContext.getString(id)

typealias stringResourceWithArguments = (Int, List<Any?>) -> String
@FunBinding
fun stringResourceWithArguments(
    applicationContext: ApplicationContext,
    id: Int,
    args: List<Any?>
): String = applicationContext.getString(id, *args.toTypedArray())

typealias stringArrayResource = (Int) -> Array<String>
@FunBinding
fun stringArrayResource(applicationContext: ApplicationContext, id: Int): Array<String> =
    applicationContext.resources.getStringArray(id)
