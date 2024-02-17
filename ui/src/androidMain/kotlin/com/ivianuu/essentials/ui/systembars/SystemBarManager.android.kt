/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import android.os.*
import android.view.*
import androidx.activity.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.core.view.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.injekt.*

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
        windowInsets.top.toPx() / 2
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
          bottomPadding > 0f -> screenHeight - bottomPadding / 2
          leftPadding > 0f -> leftPadding / 2
          rightPadding > 0f -> screenWidth - rightPadding / 2
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
