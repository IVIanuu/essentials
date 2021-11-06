/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.colorpicker.ColorPickerPalette
import com.ivianuu.essentials.ui.animation.transition.ContainerTransformSurface
import com.ivianuu.essentials.ui.animation.transition.LocalSharedElementTransitionFraction
import com.ivianuu.essentials.ui.animation.transition.SharedElement
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.RootKey
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.essentials.xposed.IsXposedRunning
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide class HomeKey : RootKey

@Provide fun homeUi(
  isXposedRunning: IsXposedRunning,
  navigator: Navigator,
  itemsFactory: () -> List<HomeItem>,
  toaster: Toaster,
): KeyUi<HomeKey> = {
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
