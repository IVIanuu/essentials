package essentials.ui.material

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import essentials.ui.systembars.systemBarStyle
import essentials.ui.util.isLight

@Composable fun EsNavigationBar(
  modifier: Modifier = Modifier,
  containerColor: Color = NavigationBarDefaults.containerColor,
  contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
  tonalElevation: Dp = NavigationBarDefaults.Elevation,
  windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
  applySystemBarStyle: Boolean = true,
  content: @Composable RowScope.() -> Unit
) {
  val systemBarStyleModifier = if (applySystemBarStyle) {
    Modifier.systemBarStyle(
      bgColor = Color.Transparent,
      darkIcons = containerColor.isLight
    )
  } else Modifier

  NavigationBar(
    modifier.then(systemBarStyleModifier),
    containerColor,
    contentColor,
    tonalElevation,
    windowInsets,
    content
  )
}
