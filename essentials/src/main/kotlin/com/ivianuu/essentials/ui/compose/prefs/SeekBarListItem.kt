package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun SeekBarListItem(
    text: @Composable() (() -> Unit),
    icon: @Composable() (() -> Unit)? = null,
    secondaryText: @Composable() (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable() (() -> Unit)? = null
) = composable("SeekBarListItem") {

    /*ListItem(

    )*/
}