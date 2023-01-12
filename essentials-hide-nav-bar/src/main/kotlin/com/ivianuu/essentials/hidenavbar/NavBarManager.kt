/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.graphics.Rect
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

context(
AppContext,
CombinedForceNavBarVisibleProvider,
DisplayRotation.Provider,
Logger,
NonSdkInterfaceDetectionDisabler,
OverscanUpdater,
PermissionManager
) @Provide fun navBarManager(
  navBarFeatureSupported: NavBarFeatureSupported,
  pref: DataStore<NavBarPrefs>
) = ScopeWorker<AppScope> worker@{
  if (!navBarFeatureSupported.value) return@worker
  permissionState(listOf<TypeKey<NavBarPermission>>())
    .flatMapLatest { hasPermission ->
      if (hasPermission) {
        forceNavBarVisible
          .flatMapLatest { forceVisible ->
            pref.data
              // we wanna ignore changes to the wasNavBarHidden state
              .distinctUntilChangedBy { it.copy(wasNavBarHidden = false) }
              .map {
                if (!forceVisible) it
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
    .collect { it.apply() }
}

private sealed interface NavBarState {
  data class Hidden(val rotationMode: NavBarRotationMode, val rotation: DisplayRotation) : NavBarState
  object Visible : NavBarState
}

context(AppContext, Logger, NonSdkInterfaceDetectionDisabler, OverscanUpdater)
    private suspend fun NavBarState.apply() {
  log { "apply nav bar state $this" }
  catch {
    catch {
      // ensure that we can access non sdk interfaces
      disableNonSdkInterfaceDetection()
    }.onFailure { it.printStackTrace() }

    val rect = when (this) {
      is NavBarState.Hidden -> getOverscanRect(
        -getNavigationBarHeight(rotation), rotationMode, rotation
      )
      NavBarState.Visible -> Rect(0, 0, 0, 0)
    }
    updateOverscan(rect)
  }.onFailure {
    log(Logger.Priority.ERROR) { "Failed to apply nav bar state ${it.asLog()}" }
  }
}

context(AppContext) private fun getNavigationBarHeight(rotation: DisplayRotation): Int {
  val name = if (rotation.isPortrait) "navigation_bar_height"
  else "navigation_bar_width"
  val id = resources.getIdentifier(name, "dimen", "android")
  return if (id > 0) resources.getDimensionPixelSize(id) else 0
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
