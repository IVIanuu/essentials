/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import android.os.*
import android.view.*
import androidx.activity.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.core.view.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

@Provide /*actual*/ val systemBarManagerProvider = SystemBarManagerProvider { content ->
  val systemBarManager = remember { AndroidSystemBarManager() }
  systemBarManager.Apply()
  CompositionLocalProvider(
    LocalSystemBarManager provides systemBarManager,
    content = content
  )
}

private class AndroidSystemBarManager : SystemBarManager {
  private var styles by mutableStateOf(emptyList<SystemBarStyle>())

  override fun registerStyle(style: SystemBarStyle) {
    styles = (styles + style)
      .sortedByDescending { it.zIndex }
  }

  override fun unregisterStyle(style: SystemBarStyle) {
    styles -= (styles + style)
      .sortedByDescending { it.zIndex }
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
    val statusBarHitPointY by rememberUpdatedState(
      with(density) {
        WindowInsets.statusBars.getTop(density) / 2f
      }
    )

    val statusBarColor by remember {
      derivedStateOf {
        styles
          .lastOrNull { statusBarHitPointY in it.bounds.top..it.bounds.bottom }
          ?.barColor
      }
    }

    DisposableEffect(activity, statusBarColor) {
      activity.window.statusBarColor =
        (statusBarColor ?: Color.Black.copy(alpha = 0.2f)).toArgb()
      onDispose {  }
    }

    val statusBarDarkIcons by remember {
      derivedStateOf {
        styles
          .lastOrNull { statusBarHitPointY in it.bounds.top..it.bounds.bottom }
          ?.darkIcons
      }
    }

    DisposableEffect(activity, statusBarDarkIcons) {
      activity.window.decorView.systemUiVisibility =
        activity.window.decorView.systemUiVisibility.setFlag(
          View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
          statusBarDarkIcons == true
        )
      onDispose { }
    }

    val screenHeight = activity.window.decorView.height.toFloat()
    val screenWidth = activity.window.decorView.width.toFloat()
    val navBarHitPointY = with(density) {
      val windowInsets = WindowInsets.navigationBars
      val bottomPadding = windowInsets.getBottom(density)
      val leftPadding = windowInsets.getLeft(density, LocalLayoutDirection.current)
      val rightPadding = windowInsets.getRight(density, LocalLayoutDirection.current)
      when {
        bottomPadding > 0f -> screenHeight - bottomPadding / 2f
        leftPadding > 0f -> leftPadding / 2f
        rightPadding > 0f -> screenWidth - rightPadding / 2f
        else -> 0f
      }
    }

    val navBarColor by remember {
      derivedStateOf {
        styles
          .lastOrNull { navBarHitPointY in it.bounds.top..it.bounds.bottom }
          ?.barColor
      }
    }

    DisposableEffect(activity, navBarColor) {
      activity.window.navigationBarColor =
        (
            navBarColor ?: Color.Black
              .copy(alpha = 0.2f)
            ).toArgb()
      onDispose { }
    }

    val navBarDarkIcons by remember {
      derivedStateOf {
        styles
          .lastOrNull { navBarHitPointY in it.bounds.top..it.bounds.bottom }
          ?.darkIcons
      }
    }

    DisposableEffect(activity, navBarColor) {
      activity.window.decorView.systemUiVisibility =
        activity.window.decorView.systemUiVisibility.setFlag(
          View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
          navBarDarkIcons == true
        )
      onDispose {  }
    }
  }
}
