package com.ivianuu.essentials.ui.compose.prefs

/**
@Composable
fun SliderListItem(
slider: @Composable() (() -> Unit),
text: @Composable() (() -> Unit),
icon: @Composable() (() -> Unit)? = null,
secondaryText: @Composable() (() -> Unit)? = null,
singleLineSecondaryText: Boolean = true,
overlineText: @Composable() (() -> Unit)? = null
) = composable("SliderListItem") {
Stack {
ListItem(text, icon, secondaryText, singleLineSecondaryText, overlineText)

Padding(left = 16.dp, top = 8.dp, right = 16.dp, bottom = 16.dp) {
slider()
}
}
}*/