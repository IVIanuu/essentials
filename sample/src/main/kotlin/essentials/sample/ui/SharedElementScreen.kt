package essentials.sample.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Provide val SharedElementHomeItem = HomeItem(
  title = "Shared element",
  { index, color -> SharedElementScreen(index, color) }
) { SharedElementScreen(-1, it) }

class SharedElementScreen(val index: Int, val color: Color) : Screen<Unit>

const val ColorKey = "image"
const val ContainerKey = "container"

@Provide @Composable fun SharedElementUi(
  screen: SharedElementScreen
): Ui<SharedElementScreen> {
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
