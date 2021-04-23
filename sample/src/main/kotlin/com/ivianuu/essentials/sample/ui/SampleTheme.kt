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

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.twilight.domain.*
import com.ivianuu.essentials.twilight.ui.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Given
fun sampleTheme(@Given twilightState: StateFlow<TwilightState>): AppTheme = { content ->
    TwilightTheme(
        shapes = Shapes(
            medium = RoundedCornerShape(12.dp)
        ),
        twilightState = twilightState.collectAsState().value
    ) {
        CompositionLocalProvider(
            LocalStackTransition provides remember { ScaledSharedAxisStackTransition() },
            content = content
        )
    }
}
