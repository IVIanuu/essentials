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

import com.ivianuu.essentials.ui.compose.es.ComposeActivity
import com.ivianuu.essentials.ui.compose.navigation.DefaultRouteTransitionAmbient
import com.ivianuu.essentials.ui.compose.navigation.HorizontalRouteTransition
import com.ivianuu.essentials.ui.compose.navigation.Route

class MainActivity : ComposeActivity() {

    override val startRoute: Route
        get() = HomeRoute

    override fun content() {
        DefaultRouteTransitionAmbient.Provider(value = HorizontalRouteTransition()) {
            super.content()
        }
    }

}