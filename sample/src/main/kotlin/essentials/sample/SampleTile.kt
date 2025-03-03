package essentials.sample

import androidx.compose.runtime.*
import essentials.*
import essentials.tile.*
import injekt.*

@Provide fun sampleTilePresenter(resources: Resources) = Presenter {
  var count by remember { mutableIntStateOf(0) }
  TileState<EsTileService1>(
    icon = resources.loadIcon(essentials.gestures.R.drawable.ic_action_recent_apps),
    label = "Count $count",
    onClick = { count++ }
  )
}
