package essentials.sample

import android.graphics.drawable.Icon
import androidx.compose.runtime.*
import essentials.*
import essentials.app.*
import essentials.tile.*
import injekt.*

@Provide @AndroidComponent class SampleTile(
  @property:Provide private val scope: Scope<*> = inject
) : EsTileService() {
  @Composable override fun state(): TileState {
    var count by remember { mutableIntStateOf(0) }
    return TileState(
      icon = Icon.createWithResource(
        appContext(),
        essentials.gestures.R.drawable.ic_action_recent_apps
      ),
      label = "Count $count",
      onClick = { count++ }
    )
  }
}
