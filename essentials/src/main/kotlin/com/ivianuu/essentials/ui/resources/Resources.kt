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

package com.ivianuu.essentials.ui.resources

import androidx.compose.Composable
import androidx.compose.remember
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Image
import com.ivianuu.essentials.ui.image.BitmapImage

// todo remove

@Composable
fun drawableResource(resId: Int): Image {
    val context = ContextAmbient.current
    return remember { BitmapImage(ContextCompat.getDrawable(context, resId)!!.toBitmap()) }
}
