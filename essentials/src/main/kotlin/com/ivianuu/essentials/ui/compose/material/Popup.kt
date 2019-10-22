package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.dp
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Wrap
import androidx.ui.material.surface.Card
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Popup(
    alignment: Alignment,
    content: @Composable() () -> Unit
) = composable("Popup") {
    Wrap(alignment) {
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(4.dp),
            children = content
        )
    }
}

/**
scaffold.showOverlay { dismissOverlay ->
Padding(4.dp) {
Popup(
alignment = Alignment.TopRight,
content = {
ConstrainedBox(
constraints = DpConstraints(
minWidth = 200.dp,
minHeight = 100.dp
)
) {
Padding(8.dp) {
Column(
crossAxisAlignment = CrossAxisAlignment.Center
) {
Text("Item 1")
HeightSpacer(8.dp)
Text("Item 2")
}
}
}
}
)
}

+launchOnActive {
delay(3000)
dismissOverlay()
}
}*/