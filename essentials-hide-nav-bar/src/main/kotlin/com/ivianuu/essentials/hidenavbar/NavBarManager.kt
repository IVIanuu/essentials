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

package com.ivianuu.essentials.hidenavbar

import android.content.Context
import android.graphics.Rect
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.update
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.e
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Given
fun navBarManager(
    @Given appContext: AppContext,
    @Given displayRotation: Flow<DisplayRotation>,
    @Given forceNavBarVisibleState: Flow<CombinedForceNavBarVisibleState>,
    @Given navBarFeatureSupported: NavBarFeatureSupported,
    @Given logger: Logger,
    @Given nonSdkInterfaceDetectionDisabler: NonSdkInterfaceDetectionDisabler,
    @Given permissionState: Flow<PermissionState<NavBarPermission>>,
    @Given prefs: Flow<NavBarPrefs>,
    @Given setOverscan: OverscanUpdater,
    @Given wasNavBarHiddenStore: Store<WasNavBarHidden, ValueAction<WasNavBarHidden>>
): ScopeWorker<AppGivenScope> = worker@ {
    if (!navBarFeatureSupported) return@worker
    permissionState
        .flatMapLatest { hasPermission ->
            if (hasPermission) {
                forceNavBarVisibleState
                    .flatMapLatest { forceVisible ->
                        if (!forceVisible) prefs
                        else flowOf(NavBarPrefs(false, NavBarRotationMode.NOUGAT))
                    }
            } else {
                infiniteEmptyFlow()
            }
        }
        .flatMapLatest { currentPrefs ->
            logger.d { "current prefs $currentPrefs" }
            if (currentPrefs.hideNavBar) {
                displayRotation
                    .map { NavBarState.Hidden(currentPrefs.navBarRotationMode, it) }
                    .onEach { wasNavBarHiddenStore.update { true } }
            } else {
                flowOf(NavBarState.Visible)
                    .filter { wasNavBarHiddenStore.first() }
                    .onEach { wasNavBarHiddenStore.update { false } }
            }
        }
        .collect { it.apply(appContext, nonSdkInterfaceDetectionDisabler, logger, setOverscan) }
}

private sealed class NavBarState {
    data class Hidden(val rotationMode: NavBarRotationMode, val rotation: DisplayRotation) : NavBarState()
    object Visible : NavBarState()
}

private suspend fun NavBarState.apply(
    context: Context,
    disableNonSdkInterfaceDetection: NonSdkInterfaceDetectionDisabler,
    logger: Logger,
    setOverscan: OverscanUpdater
) {
    logger.d { "apply nav bar state $this" }
    runCatching {
        runCatching {
            // ensure that we can access non sdk interfaces
            disableNonSdkInterfaceDetection()
        }.onFailure { it.printStackTrace() }

        val rect = when (this) {
            is NavBarState.Hidden -> getOverscanRect(
                -getNavigationBarHeight(context, rotation), rotationMode, rotation)
            NavBarState.Visible -> Rect(0, 0, 0, 0)
        }
        setOverscan(rect)
    }.onFailure {
        logger.e(it) { "Failed to apply nav bar state $this" }
    }
}

private fun getNavigationBarHeight(
    context: Context,
    rotation: DisplayRotation
): Int {
    val name = if (rotation.isPortrait) "navigation_bar_height"
    else "navigation_bar_width"
    val id = context.resources.getIdentifier(name, "dimen", "android")
    return if (id > 0) context.resources.getDimensionPixelSize(id) else 0
}

private fun getOverscanRect(
    navBarHeight: Int,
    rotationMode: NavBarRotationMode,
    rotation: DisplayRotation
): Rect = when (rotationMode) {
    NavBarRotationMode.MARSHMALLOW -> {
        when (rotation) {
            DisplayRotation.PORTRAIT_UP -> Rect(0, 0, 0, navBarHeight)
            DisplayRotation.LANDSCAPE_LEFT -> Rect(0, 0, 0, navBarHeight)
            DisplayRotation.PORTRAIT_DOWN -> Rect(0, navBarHeight, 0, 0)
            DisplayRotation.LANDSCAPE_RIGHT -> Rect(0, navBarHeight, 0, 0)
        }
    }
    NavBarRotationMode.NOUGAT -> {
        when (rotation) {
            DisplayRotation.PORTRAIT_DOWN -> Rect(0, navBarHeight, 0, 0)
            else -> Rect(0, 0, 0, navBarHeight)
        }
    }
    NavBarRotationMode.TABLET -> {
        when (rotation) {
            DisplayRotation.PORTRAIT_UP -> Rect(0, 0, 0, navBarHeight)
            DisplayRotation.LANDSCAPE_LEFT -> Rect(navBarHeight, 0, 0, 0)
            DisplayRotation.PORTRAIT_DOWN -> Rect(0, navBarHeight, 0, 0)
            DisplayRotation.LANDSCAPE_RIGHT -> Rect(0, 0, navBarHeight, 0)
        }
    }
}
