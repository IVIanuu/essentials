package essentials.sample

import android.graphics.drawable.*
import androidx.compose.runtime.*
import essentials.app.*
import essentials.tile.*
import injekt.*

@Provide @AndroidComponent class SampleTile : EsTileService() {
  @Composable override fun state(): TileState {
    var count by remember { mutableIntStateOf(0) }
    return TileState(
      icon = Icon.createWithResource(this, essentials.gestures.R.drawable.ic_action_recent_apps),
      label = "Count $count",
      onClick = { count++ }
    )
  }
}
