package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.essentials.ui.util.*

@Composable fun NavigationBar(
  modifier: Modifier = Modifier,
  backgroundColor: Color = MaterialTheme.colors.primarySurface,
  contentColor: Color = contentColorFor(backgroundColor),
  elevation: Dp = BottomNavigationDefaults.Elevation,
  applySystemBarStyle: Boolean = true,
  content: @Composable RowScope.() -> Unit
) {
  val systemBarStyleModifier = if (applySystemBarStyle) {
    Modifier.systemBarStyle(
      bgColor = Color.Transparent,
      darkIcons = backgroundColor.isLight
    )
  } else Modifier
  Surface(
    color = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = systemBarStyleModifier.then(modifier)
  ) {
    BottomNavigation(
      Modifier.navigationBarsPadding().then(modifier),
      backgroundColor,
      contentColor,
      0.dp,
      content
    )
  }
}

@Composable fun RowScope.NavigationBarItem(
  selected: Boolean,
  onClick: () -> Unit,
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  label: @Composable (() -> Unit)? = null,
  alwaysShowLabel: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  selectedContentColor: Color = LocalContentColor.current,
  unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium)
) = BottomNavigationItem(
  selected,
  onClick,
  icon,
  modifier,
  enabled,
  label,
  alwaysShowLabel,
  interactionSource,
  selectedContentColor,
  unselectedContentColor
)
