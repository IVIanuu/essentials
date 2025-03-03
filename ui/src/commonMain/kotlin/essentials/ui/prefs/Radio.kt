package essentials.ui.prefs

import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import essentials.ui.material.EsListItem

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
