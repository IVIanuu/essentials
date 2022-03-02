/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.content.*
import android.graphics.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.screenstate.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide fun navBarManager(
  context: AppContext,
  displayRotation: Flow<DisplayRotation>,
  forceNavBarVisibleState: Flow<CombinedForceNavBarVisibleState>,
  navBarFeatureSupported: NavBarFeatureSupported,
  nonSdkInterfaceDetectionDisabler: NonSdkInterfaceDetectionDisabler,
  permissionState: Flow<PermissionState<NavBarPermission>>,
  pref: DataStore<NavBarPrefs>,
  setOverscan: OverscanUpdater,
  L: Logger
) = ScopeWorker<AppScope> worker@ {
  if (!navBarFeatureSupported.value) return@worker
  permissionState
    .flatMapLatest { hasPermission ->
      if (hasPermission) {
        forceNavBarVisibleState
          .flatMapLatest { forceVisible ->
            pref.data
              // we wanna ignore changes to the wasNavBarHidden state
              .distinctUntilChangedBy { it.copy(wasNavBarHidden = false) }
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

private sealed interface NavBarState {
  data class Hidden(val rotationMode: NavBarRotationMode, val rotation: DisplayRotation) : NavBarState
  object Visible : NavBarState
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
