/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import android.app.Activity
import android.content.Context
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastFilter
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.nullOf
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.overlay.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.essentials.xposed.*
import injekt.*

@Provide class HomeScreen : RootScreen {
  @Provide companion object {
    @Provide fun ui(
      isXposedRunning: IsXposedRunning,
      itemsFactory: () -> List<HomeItem>,
      navigator: Navigator,
      toaster: Toaster
    ) = Ui<HomeScreen> {
      val finalItems = remember { itemsFactory().sortedBy { it.title } }
      EsScaffold(
        topBar = {
          EsAppBar(
            title = { Text("Home") },
            actions = {
              BottomSheetLauncherButton {
                EsListItem(
                  onClick = {
                    toaster.toast("Clicked")
                    dismiss()
                  },
                  headlineContent = { Text("Test") },
                  leadingContent = { Icon(Icons.Default.Add, null) }
                )
              }
            }
          )
        }
      ) {
        EsLazyColumn {
          itemsIndexed(finalItems) { index, item ->
            val color = rememberSaveable(item) {
              ColorPickerPalette.entries
                .fastFilter { it != ColorPickerPalette.BLACK && it != ColorPickerPalette.WHITE }
                .shuffled()
                .first()
                .front
            }

            HomeItem(
              item = item,
              index = index,
              color = color,
              onClick = action {
                navigator.push(
                  item.screenFactoryWithIndex?.invoke(index, color)
                    ?: item.screenFactory(color)
                )
              }
            )
          }

          item {
            EsListItem(
              headlineContent = {
                Text(
                  if (isXposedRunning.value) "Xposed is running"
                  else "Xposed is NOT running"
                )
              }
            )
          }
        }
      }
    }

    @Composable private fun HomeItem(
      index: Int,
      color: Color,
      onClick: () -> Unit,
      item: HomeItem
    ) {
      Surface(
        modifier = with(LocalScreenAnimationScope.current) {
          Modifier
            .sharedBounds(
              rememberSharedContentState(ContainerKey + index),
              this,
              boundsTransform = { _, _ -> tween(400) },
              enter = fadeIn(tween(400)),
              exit = fadeOut(tween(400))
            )
        },
      ) {
        EsListItem(
          onClick = onClick,
          headlineContent = {
            Text(item.title)
          },
          overlineContent = {
            Text("Overline")
          },
          supportingContent = {
            Text("Supporting")
          },
          leadingContent = {
            Box(
              modifier = with(LocalScreenAnimationScope.current) {
                Modifier
                  .size(40.dp)
                  .background(color, CircleShape)
                  .sharedElement(
                    rememberSharedContentState(ColorKey + index),
                    this,
                    boundsTransform = { _, _ -> tween(400) }
                  )
              }
            )
          },
          trailingContent = {
            BottomSheetLauncherButton {
              (0..40).forEach { index ->
                EsListItem(
                  onClick = { dismiss() },
                  headlineContent = { Text(index.toString()) },
                  leadingContent = { Icon(Icons.Default.Add, null) }
                )
              }
            }
          }
        )
      }
    }
  }
}

data class HomeItem(
  val title: String,
  val screenFactoryWithIndex: ((Int, Color) -> Screen<*>)? = null,
  val screenFactory: (Color) -> Screen<*>
)
