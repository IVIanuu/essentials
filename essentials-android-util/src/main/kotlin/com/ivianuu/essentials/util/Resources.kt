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
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationContext

typealias bitmapResource = (Int) -> ImageAsset
@Binding
fun bitmapResource(applicationContext: ApplicationContext): bitmapResource = { id ->
    applicationContext.getDrawable(id)!!.toImageAsset()
}

typealias booleanResource = (Int) -> Boolean
@Binding
fun booleanResource(applicationContext: ApplicationContext): booleanResource = { id ->
    applicationContext.resources.getBoolean(id)
}

typealias colorResource = (Int) -> Color
@Binding
fun colorResource(applicationContext: ApplicationContext): colorResource = { id ->
    Color(applicationContext.getColor(id))
}

typealias dimensionResource = (Int) -> Dp
@Binding
fun dimensionResource(applicationContext: ApplicationContext): dimensionResource = { id ->
    with(Density(applicationContext)) {
        applicationContext.resources.getDimension(id).toInt().toDp()
    }
}

typealias drawableResource = (Int) -> ImageAsset
@Binding
fun drawableResource(bitmapResource: bitmapResource): drawableResource = { id ->
    bitmapResource(id)
}

typealias floatResource = (Int) -> Float
@Binding
fun floatResource(applicationContext: ApplicationContext): floatResource = { id ->
    ResourcesCompat.getFloat(applicationContext.resources, id)
}

typealias fontResource = (Int) -> Font
@Binding
fun fontResource(applicationContext: ApplicationContext): fontResource = { id ->
    font(id)
}

typealias intResource = (Int) -> Int
@Binding
fun intResource(applicationContext: ApplicationContext): intResource = { id ->
    applicationContext.resources.getInteger(id)
}

typealias intArrayResource = (Int) -> IntArray
@Binding
fun intArrayResource(applicationContext: ApplicationContext): intArrayResource = { id ->
    applicationContext.resources.getIntArray(id)
}

typealias stringResource = (Int) -> String
@Binding
fun stringResource(applicationContext: ApplicationContext): stringResource = { id ->
    applicationContext.getString(id)
}

typealias stringResourceWithArguments = (Int, List<Any?>) -> String
@Binding
fun stringResourceWithArguments(
    applicationContext: ApplicationContext
): stringResourceWithArguments = { id, args ->
    applicationContext.getString(id, *args.toTypedArray())
}

typealias stringArrayResource = (Int) -> Array<String>
@Binding
fun stringArrayResource(applicationContext: ApplicationContext): stringArrayResource = { id ->
    applicationContext.resources.getStringArray(id)
}
