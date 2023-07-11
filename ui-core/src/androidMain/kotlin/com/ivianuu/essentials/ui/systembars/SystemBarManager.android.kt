/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.setFlag
import com.ivianuu.essentials.ui.insets.LocalInsets
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

    val windowInsets = LocalInsets.current
    val density = LocalDensity.current

    val statusBarHitPointY = remember(density, windowInsets) {
      with(density) {
        windowInsets.top.toPx()
      }
    }

    val statusBarStyle = styles
      .sortedBy { it.zIndex }
      .lastOrNull { statusBarHitPointY in it.bounds.top..it.bounds.bottom }

    DisposableEffect(activity, statusBarStyle?.barColor, statusBarStyle?.darkIcons) {
      activity.window.statusBarColor =
        (statusBarStyle?.barColor ?: Color.Black.copy(alpha = 0.2f)).toArgb()
      activity.window.decorView.systemUiVisibility =
        activity.window.decorView.systemUiVisibility.setFlag(
          View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
          statusBarStyle?.darkIcons ?: false
        )
      onDispose { }
    }

    val screenHeight = activity.window.decorView.height.toFloat()
    val screenWidth = activity.window.decorView.width.toFloat()
    val navBarHitPointY = remember(density, windowInsets, screenWidth, screenHeight) {
      with(density) {
        val bottomPadding = windowInsets.bottom.toPx()
        val leftPadding = windowInsets.left.toPx()
        val rightPadding = windowInsets.right.toPx()
        when {
          bottomPadding > 0f -> screenHeight - bottomPadding
          leftPadding > 0f -> leftPadding
          rightPadding > 0f -> screenWidth - rightPadding
          else -> 0f
        }
      }
    }

    val navBarStyle = styles
      .sortedBy { it.zIndex }
      .lastOrNull { navBarHitPointY in it.bounds.top..it.bounds.bottom }

    DisposableEffect(activity, navBarStyle?.barColor, navBarStyle?.darkIcons) {
      activity.window.navigationBarColor =
        (
            navBarStyle?.barColor ?: Color.Black
              .copy(alpha = 0.2f)
            ).toArgb()
      activity.window.decorView.systemUiVisibility =
        activity.window.decorView.systemUiVisibility.setFlag(
          View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
          navBarStyle?.darkIcons ?: false
        )
      onDispose { }
    }
  }
}
