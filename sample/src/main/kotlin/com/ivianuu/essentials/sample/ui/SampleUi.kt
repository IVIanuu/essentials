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

package com.ivianuu.essentials.sample.ui

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.remember
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.twilight.TwilightTheme
import com.ivianuu.essentials.ui.core.AppUi
import com.ivianuu.essentials.ui.core.BindAppUi
import com.ivianuu.essentials.ui.core.BindUiInitializer
import com.ivianuu.essentials.ui.core.ProvideSystemBarStyle
import com.ivianuu.essentials.ui.core.SystemBarStyle
import com.ivianuu.essentials.ui.core.UiInitializer
import com.ivianuu.essentials.ui.navigation.DefaultRouteTransitionAmbient
import com.ivianuu.essentials.ui.navigation.InjectedNavigator
import com.ivianuu.essentials.ui.navigation.transition.VerticalFadeRouteTransition
import com.ivianuu.essentials.util.isDark
import com.ivianuu.injekt.Transient

@BindAppUi
@Transient
class SampleUi : AppUi {
    @Composable
    override fun runApp() {
        ProvideSystemBarStyle(
            SystemBarStyle(
                statusBarColor = Color.Black.copy(alpha = 0.2f),
                lightStatusBar = MaterialTheme.colors.onPrimary.isDark,
                navigationBarColor = MaterialTheme.colors.surface.copy(alpha = 0.7f),
                lightNavigationBar = MaterialTheme.colors.onSurface.isDark
            )
        ) {
            InjectedNavigator(startRoute = HomeRoute)
        }
    }
}

@BindUiInitializer
@Transient
class SampleUiInitializer : UiInitializer {
    @Composable
    override fun apply(children: @Composable () -> Unit) {
        Providers(
            DefaultRouteTransitionAmbient provides remember { VerticalFadeRouteTransition() }
        ) {
            TwilightTheme(children = children)
        }
    }
}
