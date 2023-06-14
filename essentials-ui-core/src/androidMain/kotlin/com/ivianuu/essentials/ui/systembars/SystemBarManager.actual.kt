/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.view.WindowCompat
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.setFlag
import com.ivianuu.injekt.Provide

@Provide actual val systemBarManagerProvider = SystemBarManagerProvider { content ->
  val systemBarManager = remember { AndroidSystemBarManager() }
  systemBarManager.Apply()
  CompositionLocalProvider(
    LocalSystemBarManager provides systemBarManager,
    content = content
  )
}

private class AndroidSystemBarManager : SystemBarManager {
  private val styles = mutableStateListOf<SystemBarStyle>()

  override fun registerStyle(style: SystemBarStyle) {
    styles += style
  }

  override fun unregisterStyle(style: SystemBarStyle) {
    styles -= style
  }

  @Composable fun Apply() {
    val activity = LocalContext.current.cast<ComponentActivity>()
    DisposableEffect(activity) {
      WindowCompat.setDecorFitsSystemWindows(activity.window, false)
      if (Build.VERSION.SDK_INT >= 29) {
        activity.window.isStatusBarContrastEnforced = false
        activity.window.isNavigationBarContrastEnforced = false
      }
      onDispose { }
    }

    val density = LocalDensity.current
    val windowInsets = WindowInsets.statusBars
    val layoutDirection = LocalLayoutDirection.current

    val statusBarHitPoint = remember(density, windowInsets) {
      Offset(
        windowInsets.getLeft(density, layoutDirection).toFloat(),
        windowInsets.getTop(density) * 0.5f
      )
    }

    val statusBarStyle = styles
      .sortedBy { it.elevation }
      .lastOrNull { it.bounds.contains(statusBarHitPoint) }

    DisposableEffect(activity, statusBarStyle?.barColor, statusBarStyle?.lightIcons) {
      activity.window.statusBarColor =
        (statusBarStyle?.barColor ?: Color.Black.copy(alpha = 0.2f)).toArgb()
      activity.window.decorView.systemUiVisibility =
        activity.window.decorView.systemUiVisibility.setFlag(
          View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
          statusBarStyle?.lightIcons ?: false
        )
      onDispose { }
    }

    val screenHeight = activity.window.decorView.height.toFloat()
    val screenWidth = activity.window.decorView.width.toFloat()
    val navBarHitPoint = remember(density, layoutDirection, windowInsets, screenWidth, screenHeight) {
      with(density) {
        val bottomPadding = windowInsets.getBottom(density).toFloat()
        val leftPadding = windowInsets.getLeft(density, layoutDirection)
        val rightPadding = windowInsets.getRight(density, layoutDirection)
        when {
          bottomPadding > 0f -> Offset(bottomPadding, screenHeight - bottomPadding * 0.5f)
          leftPadding > 0f -> Offset(leftPadding * 0.5f, screenHeight)
          rightPadding > 0f -> Offset(screenWidth - rightPadding * 0.5f, screenHeight)
          else -> Offset(0f, 0f)
        }
      }
    }

    val navBarStyle = styles
      .sortedBy { it.elevation }
      .lastOrNull { it.bounds.contains(navBarHitPoint) }

    DisposableEffect(activity, navBarStyle?.barColor, navBarStyle?.lightIcons) {
      activity.window.navigationBarColor =
        (
            navBarStyle?.barColor ?: Color.Black
              .copy(alpha = 0.2f)
            ).toArgb()
      activity.window.decorView.systemUiVisibility =
        activity.window.decorView.systemUiVisibility.setFlag(
          View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
          navBarStyle?.lightIcons ?: false
        )
      onDispose { }
    }
  }
}