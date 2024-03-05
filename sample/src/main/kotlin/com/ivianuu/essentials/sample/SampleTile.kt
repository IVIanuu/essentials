package com.ivianuu.essentials.sample

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.tile.*
import com.ivianuu.injekt.*

@Provide fun sampleTilePresenter(resources: Resources) = Presenter {
  var count by remember { mutableIntStateOf(0) }
  TileState<EsTileService1>(
    icon = resources.load(com.ivianuu.essentials.gestures.R.drawable.ic_action_recent_apps),
    label = "Count $count",
    onClick = { count++ }
  )
}
