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

package com.ivianuu.essentials.ui.systembars

import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
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
import androidx.core.view.WindowCompat
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.setFlag
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.injekt.Provide

@Provide actual val systemBarManagerProvider: UiDecorator<SystemBarManagerProvider> = { content ->
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
