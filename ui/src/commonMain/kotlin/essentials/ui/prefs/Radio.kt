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
  sectionType: SectionType = SectionType.MIDDLE,
  supportingContent: (@Composable () -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
) {
  SectionListItem(
    sectionType = sectionType,
    selected = value,
    modifier = modifier,
    onClick = { onValueChange(!value) },
    headlineContent = headlineContent,
    supportingContent = supportingContent,
    leadingContent = leadingContent,
    trailingContent = { RadioButton(selected = value, onClick = null) }
  )
}
