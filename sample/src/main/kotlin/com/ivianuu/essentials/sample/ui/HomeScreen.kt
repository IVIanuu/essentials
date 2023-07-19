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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.RootScreen
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.xposed.IsXposedRunning
import com.ivianuu.injekt.Provide

@Provide class HomeScreen : RootScreen

@Provide fun homeUi(
  isXposedRunning: IsXposedRunning,
  itemsFactory: () -> List<HomeItem>,
  navigator: Navigator,
  toaster: Toaster
) = Ui<HomeScreen, Unit> {
  val finalItems = remember { itemsFactory().sortedBy { it.title } }
  Scaffold(
    topBar = {
      AppBar(
        title = { Text("Home") },
        actions = {
          PopupMenuButton {
            listOf("Option 1", "Option 2", "Option 3").forEach { title ->
              PopupMenuItem(onSelected = { toaster("Selected $title") }) {
                Text(title)
              }
            }
          }
        }
      )
    }
  ) {
    VerticalList {
      items(finalItems) { item ->
        val color = rememberSaveable(item) {
          ColorPickerPalette.entries
            .filter { it != ColorPickerPalette.BLACK && it != ColorPickerPalette.WHITE }
            .shuffled()
            .first()
            .front
        }

        HomeItem(
          item = item,
          color = color,
          onClick = action { navigator.push(item.screenFactory(color)) }
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
}

@Composable private fun HomeItem(color: Color, onClick: () -> Unit, item: HomeItem) {
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
      PopupMenuButton {
        (0..100).forEach { index ->
          PopupMenuItem(onSelected = {}) {
            Text(index.toString())
          }
        }
      }
    }
  )
}

data class HomeItem(val title: String, val screenFactory: (Color) -> Screen<*>)
