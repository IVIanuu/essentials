package essentials.sample

import androidx.compose.runtime.*
import essentials.*
import essentials.app.AndroidComponent
import essentials.tile.*
import injekt.*

@Provide @AndroidComponent class SampleTile(private val resources: Resources) : EsTileService() {
  @Composable override fun state(): TileState {
    var count by remember { mutableIntStateOf(0) }
    return TileState(
      icon = resources.loadIcon(essentials.gestures.R.drawable.ic_action_recent_apps),
      label = "Count $count",
      onClick = { count++ }
    )
  }
}
