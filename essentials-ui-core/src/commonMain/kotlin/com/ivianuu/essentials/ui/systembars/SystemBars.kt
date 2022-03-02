/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.systembars

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.util.*
import com.ivianuu.injekt.*

@Composable fun overlaySystemBarBgColor(color: Color) =
  if (color.isLight) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.2f)

@Composable fun Modifier.systemBarStyle(
  bgColor: Color = overlaySystemBarBgColor(MaterialTheme.colors.surface),
  lightIcons: Boolean = bgColor.isLight,
  elevation: Dp = 0.dp
): Modifier = composed {
  val style = remember { SystemBarStyle(bgColor, lightIcons, elevation) }
  SideEffect {
    style.barColor = bgColor
    style.lightIcons = lightIcons
    style.elevation = elevation
  }

  val systemBarManager = LocalSystemBarManager.current
  DisposableEffect(systemBarManager, style) {
    systemBarManager.registerStyle(style)
    onDispose { systemBarManager.unregisterStyle(style) }
  }

  onGloballyPositioned { style.bounds = it.boundsInWindow() }
}

fun interface RootSystemBarsStyle : UiDecorator

@Provide val rootSystemBarsStyle = RootSystemBarsStyle { content ->
  Surface {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .systemBarStyle()
    ) {
      content()
    }
  }
}

interface SystemBarManager {
  fun registerStyle(style: SystemBarStyle)

  fun unregisterStyle(style: SystemBarStyle)
}


class SystemBarStyle(barColor: Color, lightIcons: Boolean, elevation: Dp) {
  var barColor by mutableStateOf(barColor)
  var lightIcons by mutableStateOf(lightIcons)
  var bounds by mutableStateOf(Rect(0f, 0f, 0f, 0f))
  var elevation by mutableStateOf(elevation)
}

fun interface SystemBarManagerProvider : UiDecorator

@Provide expect val systemBarManagerProvider: SystemBarManagerProvider

@Provide val systemBarManagerProviderLoadingOrder = LoadingOrder<SystemBarManagerProvider>()
  .after<WindowInsetsProvider>()

@Provide val rootSystemBarsStyleLoadingOrder = LoadingOrder<RootSystemBarsStyle>()
  .after<AppTheme>()
  .after<SystemBarManagerProvider>()

val LocalSystemBarManager = staticCompositionLocalOf<SystemBarManager> {
  error("No system bar manager provided")
}
