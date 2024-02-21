/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.essentials.xposed.*
import com.ivianuu.injekt.*

@Provide class HomeScreen : RootScreen {
  @Provide companion object {
    @Provide fun ui(
      isXposedRunning: IsXposedRunning,
      itemsFactory: () -> List<HomeItem>,
      navigator: Navigator,
      toaster: Toaster
    ) = Ui<HomeScreen> {
      val finalItems = remember { itemsFactory().sortedBy { it.title } }
      ScreenScaffold(
        topBar = {
          AppBar(
            title = { Text("Home") },
            actions = {
              DropdownMenuButton {
                listOf("Option 1", "Option 2", "Option 3").forEach { title ->
                  DropdownMenuItem(onClick = { toaster("Selected $title") }) {
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
        onClick = onClick,
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
          DropdownMenuButton {
            (0..100).forEach { index ->
              DropdownMenuItem(onClick = {}) {
                Text(index.toString())
              }
            }
          }
        }
      )
    }
  }
}

data class HomeItem(val title: String, val screenFactory: (Color) -> Screen<*>)
