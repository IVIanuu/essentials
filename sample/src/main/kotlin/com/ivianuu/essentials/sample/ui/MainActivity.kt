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

import androidx.compose.remember
import com.ivianuu.essentials.twilight.TwilightTheme
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.navigation.DefaultRouteTransitionAmbient
import com.ivianuu.essentials.ui.navigation.VerticalFadeRouteTransition
import kotlin.time.milliseconds

class MainActivity : EsActivity() {

    override fun content() {
        TwilightTheme {
            DefaultRouteTransitionAmbient.Provider(
                value = remember { VerticalFadeRouteTransition(duration = 300.milliseconds) }
            ) {
                Navigator(startRoute = HomeRoute)
            }
        }
    }

}