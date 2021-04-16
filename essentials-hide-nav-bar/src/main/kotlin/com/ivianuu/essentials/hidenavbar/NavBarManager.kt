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

import android.content.*
import android.graphics.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.screenstate.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

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
    @Given wasNavBarHiddenPref: DataStore<WasNavBarHidden>
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
                    .onEach { wasNavBarHiddenPref.updateData { true } }
            } else {
                flowOf(NavBarState.Visible)
                    .filter { wasNavBarHiddenPref.data.first() }
                    .onEach { wasNavBarHiddenPref.updateData { false } }
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
