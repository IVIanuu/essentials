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

import androidx.compose.FrameManager
import androidx.lifecycle.lifecycleScope
import com.ivianuu.director.DefaultChangeHandler
import com.ivianuu.director.common.changehandler.HorizontalChangeHandler
import com.ivianuu.director.common.changehandler.VerticalChangeHandler
import com.ivianuu.essentials.legacy.ui.changehandler.OpenCloseChangeHandler
import com.ivianuu.essentials.legacy.ui.changehandler.VerticalFadeChangeHandler
import com.ivianuu.essentials.legacy.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.legacy.ui.navigation.director.ControllerRouteOptions
import com.ivianuu.essentials.legacy.ui.navigation.director.handler
import com.ivianuu.essentials.ui.compose.navigation.DefaultRouteTransitionKey
import com.ivianuu.essentials.ui.compose.navigation.FadeRouteTransitionKey
import com.ivianuu.essentials.ui.compose.navigation.HorizontalRouteTransitionKey
import com.ivianuu.essentials.ui.compose.navigation.Navigator
import com.ivianuu.essentials.ui.compose.navigation.NavigatorState
import com.ivianuu.essentials.ui.compose.navigation.OpenCloseRouteTransitionKey
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.ui.compose.navigation.VerticalFadeRouteTransitionKey
import com.ivianuu.essentials.ui.compose.navigation.VerticalRouteTransitionKey
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Route.asComposeControllerRoute(
    options: ControllerRoute.Options? = extractDefaultControllerRouteOptions()
) = ControllerRoute<RouteCompatController>(
    options = options
) {
    parametersOf(copy(enterTransition = null, exitTransition = null))
}

fun Route.extractDefaultControllerRouteOptions(): ControllerRoute.Options {
    val removesFromViewOnPush = !opaque

    val handler = when (enterTransition?.key) {
        DefaultRouteTransitionKey -> DefaultChangeHandler(removesFromViewOnPush = removesFromViewOnPush)
        FadeRouteTransitionKey -> HorizontalChangeHandler(
            duration = enterTransition!!.duration.toLongMilliseconds(),
            removesFromViewOnPush = removesFromViewOnPush
        )
        HorizontalRouteTransitionKey -> HorizontalChangeHandler(
            duration = enterTransition!!.duration.toLongMilliseconds(),
            removesFromViewOnPush = removesFromViewOnPush
        )
        OpenCloseRouteTransitionKey -> OpenCloseChangeHandler(
            duration = enterTransition!!.duration.toLongMilliseconds(),
            removesFromViewOnPush = removesFromViewOnPush
        )
        VerticalRouteTransitionKey -> VerticalChangeHandler(
            duration = enterTransition!!.duration.toLongMilliseconds(),
            removesFromViewOnPush = removesFromViewOnPush
        )
        VerticalFadeRouteTransitionKey -> VerticalFadeChangeHandler(
            duration = enterTransition!!.duration.toLongMilliseconds(),
            removesFromViewOnPush = removesFromViewOnPush
        )
        else -> DefaultChangeHandler(removesFromViewOnPush = removesFromViewOnPush)
    }

    return ControllerRouteOptions()
        .handler(handler)
}

@Factory
internal class RouteCompatController(
    @Param private val composeRoute: Route
) : ComposeController() {

    private val navigatorState: NavigatorState by inject()

    override fun modules(): List<Module> =
        listOf(RouteCompatModule(coroutineScope = lifecycleScope))

    init {
        FrameManager.ensureStarted()
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            val backStack = if (navigator.backStack.indexOf(route) > 1) {
                listOf(DummyRoute, composeRoute)
            } else {
                listOf(composeRoute)
            }

            navigatorState.setBackStackSuspend(backStack, true)

            val result = navigatorState.awaitResult<Any?>(route = composeRoute)
            navigator.pop(result = result)
        }
    }

    override fun content() {
        Navigator(state = navigatorState)
    }
}

private val DummyRoute = Route {}

private fun RouteCompatModule(coroutineScope: CoroutineScope) = Module {
    single { NavigatorState(coroutineScope = coroutineScope) }
}