package com.ivianuu.essentials.sample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.tile.EsTileService1
import com.ivianuu.essentials.tile.TileState
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.injekt.Provide

@Provide fun sampleTilePresenter(resources: Resources) = Presenter {
  var count by remember { mutableIntStateOf(0) }
  TileState<EsTileService1>(
    icon = resources(com.ivianuu.essentials.torch.R.drawable.ic_flashlight_on),
    label = "Count $count",
    onClick = { count++ }
  )
}
