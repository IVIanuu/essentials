package essentials.ui.prefs

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import essentials.ui.material.*

@Composable fun RadioListItem(
  value: Boolean,
  onValueChange: (Boolean) -> Unit,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  supportingContent: (@Composable () -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
) {
  EsListItem(
    modifier = modifier,
    onClick = { onValueChange(!value) },
    headlineContent = headlineContent,
    supportingContent = supportingContent,
    leadingContent = leadingContent,
    trailingContent = { RadioButton(selected = value, onClick = null) }
  )
}
