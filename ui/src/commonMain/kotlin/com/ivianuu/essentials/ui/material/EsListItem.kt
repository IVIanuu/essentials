package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable fun EsListItem(
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  overlineContent: @Composable (() -> Unit)? = null,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  colors: ListItemColors = ListItemDefaults.colors(
    containerColor = Color.Transparent
  ),
  tonalElevation: Dp = ListItemDefaults.Elevation,
  shadowElevation: Dp = ListItemDefaults.Elevation,
) {
  ListItem(
    headlineContent = headlineContent,
    modifier = modifier
      .then(
        if (onClick != null) Modifier.clickable(onClick = onClick)
        else Modifier
      ),
    overlineContent = overlineContent,
    supportingContent = supportingContent,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    colors = colors,
    tonalElevation = tonalElevation,
    shadowElevation = shadowElevation
  )
}
