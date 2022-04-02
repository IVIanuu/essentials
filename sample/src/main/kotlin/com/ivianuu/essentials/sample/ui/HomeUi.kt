/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.colorpicker.ColorPickerPalette
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.RootKey
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.essentials.xposed.IsXposedRunning
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide class HomeKey : RootKey

@Provide fun homeUi(
  isXposedRunning: IsXposedRunning,
  navigator: Navigator,
  itemsFactory: () -> List<HomeItem>,
  T: ToastContext
) = SimpleKeyUi<HomeKey> {
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
  ListItem(
    modifier = Modifier.clickable(onClick = onClick),
    title = {
      Text(
        item.title,
        style = MaterialTheme.typography.subtitle1
      )
    },
    leading = {
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(color, CircleShape)
      )
    },
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

data class HomeItem(val title: String, val keyFactory: (Color) -> Key<*>)
