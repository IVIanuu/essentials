/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.colorpicker.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.popup.*
import com.ivianuu.essentials.util.*
import com.ivianuu.essentials.xposed.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Provide class HomeKey : RootKey

@Provide fun homeUi(
  isXposedRunning: IsXposedRunning,
  navigator: Navigator,
  itemsFactory: () -> List<HomeItem>,
  T: ToastContext
) = KeyUi<HomeKey> {
  val finalItems = remember { itemsFactory().sortedBy { it.title } }
  SimpleListScreen(
    title = "Home",
    popupMenuItems = listOf("Option 1", "Option 2", "Option 3")
      .map { title ->
        PopupMenu.Item(onSelected = { showToast("Selected $title") }) {
          Text(title)
        }
      }
  ) {
    items(finalItems) { item ->
      val color = rememberSaveable(item) {
        ColorPickerPalette.values()
          .filter { it != ColorPickerPalette.BLACK && it != ColorPickerPalette.WHITE }
          .shuffled()
          .first()
          .front
      }

      val scope = rememberCoroutineScope()
      HomeItem(
        item = item,
        color = color,
        onClick = {
          scope.launch { navigator.push(item.keyFactory(color)) }
        }
      )
    }

    item {
      ListItem(
        title = {
          Text(
            if (isXposedRunning.value) "Xposed is running"
            else "Xposed is NOT running"
          )
        }
      )
    }
  }
}

@Composable private fun HomeItem(
  color: Color,
  onClick: () -> Unit,
  item: HomeItem,
) {
  ContainerTransformSurface(key = "container ${item.title}", isOpened = false) {
    ListItem(
      modifier = Modifier.clickable(onClick = onClick),
      title = {
        SharedElement(key = "title ${item.title}", isStart = true) {
          Text(
            item.title,
            style = MaterialTheme.typography.subtitle1
          )
        }
      },
      leading = { SharedCircleBadge("color ${item.title}", color, 40.dp, true) },
      trailing = {
        PopupMenuButton(
          items = listOf(1, 2, 3)
            .map { index ->
              PopupMenu.Item(onSelected = {}) {
                Text(index.toString())
              }
            }
        )
      }
    )
  }
}

@Composable fun SharedCircleBadge(
  key: Any,
  color: Color,
  size: Dp,
  isStart: Boolean
) {
  SharedElement(key = key, isStart = isStart) {
    val fraction = LocalSharedElementTransitionFraction.current
    Box(
      modifier = Modifier
        .size(size)
        .background(color, RoundedCornerShape((size / 2) * (1f - fraction)))
    )
  }
}

data class HomeItem(val title: String, val keyFactory: (Color) -> Key<*>)
