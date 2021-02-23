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

import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import com.ivianuu.essentials.twilight.domain.TwilightState
import com.ivianuu.essentials.twilight.ui.TwilightTheme
import com.ivianuu.essentials.ui.AppTheme
import com.ivianuu.essentials.ui.AppThemeBinding
import com.ivianuu.essentials.ui.animatedstack.LocalStackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.HorizontalStackTransition
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow

@AppThemeBinding
@Given
fun sampleTheme(@Given twilightState: Flow<TwilightState>): AppTheme = { content ->
    TwilightTheme(twilightState = twilightState) {
        Providers(
            LocalStackTransition provides remember { HorizontalStackTransition() },
            content = content
        )
    }
}
