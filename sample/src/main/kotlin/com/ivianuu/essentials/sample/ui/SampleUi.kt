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

import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import com.ivianuu.essentials.twilight.ui.TwilightTheme
import com.ivianuu.essentials.ui.animatedstack.DefaultStackTransitionAmbient
import com.ivianuu.essentials.ui.animatedstack.animation.HorizontalStackTransition
import com.ivianuu.essentials.ui.core.AppUiBinding
import com.ivianuu.essentials.ui.material.blackColors
import com.ivianuu.essentials.ui.material.colors
import com.ivianuu.essentials.ui.navigation.Content
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.setRootIfEmpty
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.GenerateMergeComponents

@GenerateMergeComponents
@AppUiBinding
@FunBinding
@Composable
fun SampleUi(
    homePage: HomePage,
    navigator: Navigator,
    twilightTheme: TwilightTheme,
) {
    twilightTheme(
        colors(),
        darkColors(),
        blackColors(),
        Typography()
    ) {
        Providers(
            DefaultStackTransitionAmbient provides remember { HorizontalStackTransition() }
        ) {
            navigator.setRootIfEmpty { homePage() }
            navigator.Content()
        }
    }
}
