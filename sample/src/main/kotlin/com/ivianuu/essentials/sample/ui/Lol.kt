package com.ivianuu.essentials.sample.ui

/**
fabConfiguration = Scaffold.FabConfiguration(
Scaffold.FabPosition.Center,
fab = {
val scaffold = +ambient(ScaffoldAmbient)
FloatingActionButton(
text = "Click me",
onClick = {
scaffold.showOverlay { dismissOverlay ->
Padding(4.dp) {
val opacity = +animatedFloat(0f)
Opacity(opacity = opacity.value) {
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
opacity.animateTo(
targetValue = 1f,
anim = TweenBuilder<Float>().apply { duration = 300 },
onEnd = { reason, value ->

}
)
delay(3000)
opacity.animateTo(
targetValue = 0f,
anim = TweenBuilder<Float>().apply { duration = 300 },
onEnd = { _, _ ->
dismissOverlay()
}
)
}
}
}
}
)
}
)
)*/