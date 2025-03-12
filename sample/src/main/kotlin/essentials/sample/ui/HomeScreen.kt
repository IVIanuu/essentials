/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

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
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.overlay.*
import essentials.util.*
import injekt.*

@Provide class HomeScreen : RootScreen

@Provide @Composable fun HomeUi(
  itemsFactory: () -> List<HomeItem>,
  navigator: Navigator,
  toaster: Toaster
): Ui<HomeScreen> {
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
          DefaultColorPalette
            .shuffled()
            .first()
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

data class HomeItem(
  val title: String,
  val screenFactoryWithIndex: ((Int, Color) -> Screen<*>)? = null,
  val screenFactory: (Color) -> Screen<*>
)
