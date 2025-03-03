package essentials.sample.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import essentials.ui.common.EsLazyColumn
import essentials.ui.material.EsAppBar
import essentials.ui.material.EsScaffold
import essentials.ui.navigation.LocalScreenAnimationScope
import essentials.ui.navigation.Screen
import essentials.ui.navigation.Ui
import injekt.Provide

@Provide val SharedElementHomeItem = HomeItem(
  title = "Shared element",
  { index, color -> SharedElementScreen(index, color) }
) { SharedElementScreen(-1, it) }

class SharedElementScreen(val index: Int, val color: Color) : Screen<Unit>

const val ColorKey = "image"
const val ContainerKey = "container"

@Provide fun sharedElementUi(
  screen: SharedElementScreen
) = Ui<SharedElementScreen> {
  Surface(
    shadowElevation = 8.dp,
    modifier = with(LocalScreenAnimationScope.current) {
      Modifier
        .sharedBounds(
          rememberSharedContentState(ContainerKey + screen.index),
          this,
          boundsTransform = { _, _ -> tween(400) },
          enter = fadeIn(tween(400)),
          exit = fadeOut(tween(400))
        )
    }
  ) {
    EsScaffold {
      EsLazyColumn {
        /*item {
          Box(
            modifier = with(LocalScreenAnimationScope.current) {
              Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(screen.color)
                .sharedElement(
                  rememberSharedContentState(ColorKey + screen.index),
                  this,
                  boundsTransform = { _, _ -> tween(3000) }
                )
            }
          ) {
            EsAppBar(
              colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
              )
            ) { Text("Shared elements") }
          }
        }*/

        items((0..100).toList()) { item ->
          ListItem(
            headlineContent = { Text("Item $item") }
          )
        }
      }
    }
  }
}
