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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.ui.AppTheme
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.insets.WindowInsetsProvider
import com.ivianuu.essentials.ui.util.isLight
import com.ivianuu.injekt.Provide

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

object RootSystemBarsStyle

@Provide val rootSystemBarsStyle: UiDecorator<RootSystemBarsStyle> = { content ->
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

object SystemBarManagerProvider

@Provide expect val systemBarManagerProvider: UiDecorator<SystemBarManagerProvider>

@Provide val systemBarManagerProviderLoadingOrder = LoadingOrder<UiDecorator<SystemBarManagerProvider>>()
  .after<UiDecorator<WindowInsetsProvider>>()

@Provide val rootSystemBarsStyleLoadingOrder = LoadingOrder<UiDecorator<RootSystemBarsStyle>>()
  .after<UiDecorator<AppTheme>>()
  .after<UiDecorator<SystemBarManagerProvider>>()

val LocalSystemBarManager = staticCompositionLocalOf<SystemBarManager> {
  error("No system bar manager provided")
}
