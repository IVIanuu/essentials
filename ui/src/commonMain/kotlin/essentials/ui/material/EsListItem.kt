package essentials.ui.material

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

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
