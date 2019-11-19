/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.compose.resources

import androidx.core.graphics.drawable.toBitmap
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Image
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.image.BitmapImage
import com.ivianuu.essentials.util.drawable

fun colorResource(id: Int) = androidx.ui.res.colorResource(id)()

fun drawableResource(resId: Int): Image = effect {
    val context = ambient(ContextAmbient)
    return@effect memo { BitmapImage(context.drawable(resId).toBitmap()) }
}

fun stringResource(id: Int) = androidx.ui.res.stringResource(id = id)()

fun stringResource(id: Int, vararg formatArgs: Any) =
    androidx.ui.res.stringResource(id = id, formatArgs = *formatArgs)()

fun stringArrayResource(id: Int) =
    androidx.ui.res.stringArrayResource(id = id)()

fun integerResource(id: Int) =
    androidx.ui.res.integerResource(id)()

fun integerArrayResource(id: Int) =
    androidx.ui.res.integerArrayResource(id)()

fun booleanResource(id: Int) =
    androidx.ui.res.booleanResource(id)()

fun dimensionResource(id: Int) =
    androidx.ui.res.dimensionResource(id)()