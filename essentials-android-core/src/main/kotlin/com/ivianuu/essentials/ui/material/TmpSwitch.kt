package com.ivianuu.essentials.ui.material

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.common.untrackedState

@Composable
fun TmpSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colors.secondaryVariant
) {
    var initialValueChange by untrackedState { true }
    Switch(
        checked = checked,
        onCheckedChange = {
            if (initialValueChange) initialValueChange = false
            else onCheckedChange(it)
        },
        modifier = modifier,
        enabled = enabled,
        color = color
    )
}
