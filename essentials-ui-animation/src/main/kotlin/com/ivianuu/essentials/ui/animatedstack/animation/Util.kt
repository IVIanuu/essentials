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

package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import com.ivianuu.essentials.ui.animatable.Animatable
import com.ivianuu.essentials.ui.animatable.bounds
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.rememberRef
import com.ivianuu.essentials.ui.common.setValue

@Composable
fun Animatable.rememberFirstNonNullBounds(): Rect? {
    var capturedBoundsState by rememberRef<Rect?> { null }
    if (capturedBoundsState == null && bounds != null) {
        capturedBoundsState = bounds
    }
    return capturedBoundsState
}
