/*
 * Copyright 2021 Manuel Wrage
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
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.essentials.state.asComposedFlow
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Provide fun navBarManager(
  context: AppContext,
  displayRotation: Flow<DisplayRotation>,
  forceNavBarVisibleState: @Composable () -> CombinedForceNavBarVisibleState,
  navBarFeatureSupported: NavBarFeatureSupported,
  nonSdkInterfaceDetectionDisabler: NonSdkInterfaceDetectionDisabler,
  permissionState: Flow<PermissionState<NavBarPermission>>,
  pref: DataStore<NavBarPrefs>,
  setOverscan: OverscanUpdater,
  L: Logger
) = ScopeWorker<AppComponent> worker@ {
  if (!navBarFeatureSupported.value) return@worker
  permissionState
    .flatMapLatest { hasPermission ->
      if (hasPermission) {
        forceNavBarVisibleState
          .asComposedFlow()
          .flatMapLatest { forceVisible ->
            pref.data
              .map {
                if (!forceVisible.value) it
                else it.copy(hideNavBar = false)
              }
          }
      } else {
        infiniteEmptyFlow()
      }
    }
    .distinctUntilChanged()
    .flatMapLatest { currentPrefs ->
      log { "current prefs $currentPrefs" }
      if (currentPrefs.hideNavBar) {
        displayRotation
          .map { NavBarState.Hidden(currentPrefs.navBarRotationMode, it) }
          .onEach { pref.updateData { copy(wasNavBarHidden = true) } }
      } else {
        flowOf(NavBarState.Visible)
          .filter { currentPrefs.wasNavBarHidden }
          .onEach { pref.updateData { copy(wasNavBarHidden = false) } }
      }
    }
    .distinctUntilChanged()
    .collect { it.apply(context, nonSdkInterfaceDetectionDisabler, setOverscan) }
}

private sealed class NavBarState {
  data class Hidden(val rotationMode: NavBarRotationMode, val rotation: DisplayRotation) :
    NavBarState()

  object Visible : NavBarState()
}

private suspend fun NavBarState.apply(
  context: Context,
  disableNonSdkInterfaceDetection: NonSdkInterfaceDetectionDisabler,
  setOverscan: OverscanUpdater,
  @Inject L: Logger
) {
  log { "apply nav bar state $this" }
  catch {
    catch {
      // ensure that we can access non sdk interfaces
      disableNonSdkInterfaceDetection()
    }.onFailure { it.printStackTrace() }

    val rect = when (this) {
      is NavBarState.Hidden -> getOverscanRect(
        -getNavigationBarHeight(context, rotation), rotationMode, rotation
      )
      NavBarState.Visible -> Rect(0, 0, 0, 0)
    }
    setOverscan(rect)
  }.onFailure {
    log(Logger.Priority.ERROR) { "Failed to apply nav bar state ${it.asLog()}" }
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
