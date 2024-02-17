package com.ivianuu.essentials.sample

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.tile.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide fun sampleTilePresenter(resources: Resources) = Presenter {
  var count by remember { mutableIntStateOf(0) }
  TileState<EsTileService1>(
    icon = resources(com.ivianuu.essentials.gestures.R.drawable.ic_flashlight_on),
    label = "Count $count",
    onClick = { count++ }
  )
}
