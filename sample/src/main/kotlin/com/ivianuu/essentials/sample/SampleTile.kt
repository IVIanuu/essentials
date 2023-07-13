package com.ivianuu.essentials.sample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.tile.EsTileService1
import com.ivianuu.essentials.tile.TileModel
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Provide

@Provide fun sampleTileModel(resources: Resources) = Model {
  var count by remember { mutableStateOf(0) }

  TileModel<EsTileService1>(
    icon = resources.resource(R.drawable.es_ic_flashlight_on),
    label = "Count $count",
    onClick = { count++ }
  )
}
