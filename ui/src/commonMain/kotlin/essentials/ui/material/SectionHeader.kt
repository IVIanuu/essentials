package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import essentials.ui.common.*

@Composable fun SectionHeader(
  modifier: Modifier = Modifier,
  text: @Composable () -> Unit
) {
  Box(
    modifier = modifier.padding(start = 40.dp, top = 8.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    ProvideContentColorTextStyle(
      textStyle = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.Medium
      ),
      contentColor = MaterialTheme.colorScheme.primary,
      content = text
    )
  }
}
