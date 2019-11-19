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

package com.ivianuu.essentials.ui.compose.injekt

import androidx.compose.Composable
import androidx.ui.material.ColorPalette
import androidx.ui.material.Typography
import com.ivianuu.essentials.ui.compose.material.resourceColors
import com.ivianuu.essentials.ui.compose.material.resourceTypography
import com.ivianuu.injekt.module

val composeModule = module {
    factory {
        MaterialThemeProvider(
            colors = { resourceColors() },
            typography = { resourceTypography() }
        )
    }
}

data class MaterialThemeProvider(
    val colors: @Composable() () -> ColorPalette,
    val typography: @Composable() () -> Typography
)