/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.compose.*
import com.ivianuu.injekt.scope.*

enum class AppBarStyle { PRIMARY, SURFACE }

val LocalAppBarStyle = compositionLocalOf { AppBarStyle.PRIMARY }

@Composable fun TopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = autoTopAppBarLeadingIcon(),
  actions: @Composable (() -> Unit)? = null,
  backgroundColor: Color = when (LocalAppBarStyle.current) {
    AppBarStyle.PRIMARY -> MaterialTheme.colors.primary
    AppBarStyle.SURFACE -> MaterialTheme.colors.surface
  },
  contentColor: Color = guessingContentColorFor(backgroundColor),
  elevation: Dp = DefaultAppBarElevation,
) {
  TopAppBar(
    modifier = modifier,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation
  ) {
    leading?.invoke()
    if (title != null) {
      val startPadding = if (leading != null) 16.dp else 0.dp
      val endPadding = if (actions != null) 16.dp else 0.dp
      Column(
        modifier = Modifier
          .padding(start = startPadding, end = endPadding)
          .weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        ProvideTextStyle(
          MaterialTheme.typography.h6,
          content = title
        )
      }
    }

    if (actions != null) {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        actions()
      }
    }
  }
}

@Composable fun TopAppBar(
  modifier: Modifier = Modifier,
  backgroundColor: Color = when (LocalAppBarStyle.current) {
    AppBarStyle.PRIMARY -> MaterialTheme.colors.primary
    AppBarStyle.SURFACE -> MaterialTheme.colors.surface
  },
  contentColor: Color = guessingContentColorFor(backgroundColor),
  elevation: Dp = DefaultAppBarElevation,
  applySystemBarStyle: Boolean = true,
  content: @Composable RowScope.() -> Unit,
) {
  val systemBarStyleModifier = if (applySystemBarStyle) {
    val systemBarBackgroundColor = if (LocalAppBarStyle.current == AppBarStyle.PRIMARY ||
      backgroundColor.isDark
    ) Color.Black.copy(alpha = 0.2f)
    else Color.White.copy(alpha = 0.4f)
    val lightIcons = backgroundColor.isLight
    Modifier.systemBarStyle(
      bgColor = systemBarBackgroundColor,
      lightIcons = lightIcons,
      elevation = elevation
    )
  } else Modifier
  Surface(
    color = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = systemBarStyleModifier.then(modifier)
  ) {
    InsetsPadding(left = false, right = false, bottom = false) {
      Row(
        modifier = Modifier.height(DefaultAppBarHeight)
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
      )
    }
  }
}

private val DefaultAppBarHeight = 56.dp
private val DefaultAppBarElevation = 4.dp

@Composable private fun autoTopAppBarLeadingIcon(): @Composable (() -> Unit)? {
  val component = rememberElement<AutoTopAppBarComponent>()
  val canGoBack = rememberScopedValue(key = "can_go_back") {
    component.navigator.state.value.backStack.indexOf(component.key) > 0
  }
  return when {
    canGoBack -> ({ BackButton() })
    else -> null
  }
}

@Provide @InstallElement<KeyUiScope>
class AutoTopAppBarComponent(val key: Key<*>, val navigator: Navigator)
