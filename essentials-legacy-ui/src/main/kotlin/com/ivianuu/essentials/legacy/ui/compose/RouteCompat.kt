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

package com.ivianuu.essentials.legacy.ui.compose

import androidx.compose.ambient
import androidx.lifecycle.viewModelScope
import com.ivianuu.director.DefaultChangeHandler
import com.ivianuu.essentials.legacy.ui.navigation.Navigator
import com.ivianuu.essentials.legacy.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.legacy.ui.navigation.director.ControllerRouteOptions
import com.ivianuu.essentials.legacy.ui.navigation.director.handler
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.navigation.Navigator
import com.ivianuu.essentials.ui.compose.navigation.NavigatorState
import com.ivianuu.essentials.ui.compose.navigation.RetainedNavigatorState
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.ui.compose.viewmodel.injectViewModel
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.launch

fun Route.asComposeControllerRoute(
    options: ControllerRoute.Options? = extractDefaultControllerRouteOptions(),
    popOnConfigurationChange: Boolean = false
) = ComposeControllerRoute(
    options = options,
    popOnConfigurationChange = popOnConfigurationChange
) {
    val thisRoute = this
    val composeNavigatorState = RetainedNavigatorState()

    val legacyNavigator = inject<Navigator>()
    val thisLegacyRoute = ambient(RouteAmbient)

    if (composeNavigatorState.backStack.isEmpty() &&
        legacyNavigator.backStack.indexOf(thisLegacyRoute) > 1
    ) {
        composeNavigatorState.push(DummyRoute)
    }

    injectViewModel<ResultListeningViewModel> {
        parametersOf(thisRoute, composeNavigatorState)
    }

    Navigator(state = composeNavigatorState)
}

fun Route.extractDefaultControllerRouteOptions(): ControllerRoute.Options {
    return ControllerRouteOptions()
        .handler(
            DefaultChangeHandler(removesFromViewOnPush = !opaque)
        )
}

@Factory
internal class ResultListeningViewModel(
    @Param private val route: Route,
    @Param private val composeNavigator: NavigatorState,
    private val legacyNavigator: Navigator
) : EsViewModel() {
    init {
        viewModelScope.launch {
            val result = composeNavigator.push<Any?>(route = route)
            legacyNavigator.pop(result = result)
        }
    }
}

private val DummyRoute = Route {}
