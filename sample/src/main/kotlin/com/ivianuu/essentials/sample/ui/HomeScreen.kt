/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

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
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.overlay.*
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

    @Composable private fun HomeItem(color: Color, onClick: () -> Unit, item: HomeItem) {
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
            modifier = Modifier
              .size(40.dp)
              .background(color, CircleShape)
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

data class HomeItem(val title: String, val screenFactory: (Color) -> Screen<*>)
