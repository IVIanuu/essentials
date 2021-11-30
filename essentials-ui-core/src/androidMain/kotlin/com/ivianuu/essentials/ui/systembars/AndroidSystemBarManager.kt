/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import android.os.*
import android.view.*
import androidx.activity.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.*
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

    val statusBarHitPoint = remember(density, windowInsets) {
      with(density) {
        Offset(
          windowInsets.left.toPx(),
          windowInsets.top.toPx() * 0.5f
        )
      }
    }

    val stylesSnapshot = styles.toList()

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

    if (Build.VERSION.SDK_INT >= 26) {
      val screenHeight = activity.window.decorView.height.toFloat()
      val screenWidth = activity.window.decorView.width.toFloat()
      val navBarHitPoint = remember(density, windowInsets, screenWidth, screenHeight) {
        with(density) {
          val bottomPadding = windowInsets.bottom.toPx()
          val leftPadding = windowInsets.left.toPx()
          val rightPadding = windowInsets.right.toPx()
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
    } else {
      DisposableEffect(activity) {
        activity.window.navigationBarColor = Color.Black
          .copy(alpha = 0.4f)
          .toArgb()
        onDispose { }
      }
    }
  }
}