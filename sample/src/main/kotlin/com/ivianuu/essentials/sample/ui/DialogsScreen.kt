/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.overlay.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.essentials.ui.prefs.RadioListItem
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

@Provide val dialogsHomeItem = HomeItem("Dialogs") { DialogsScreen() }

class DialogsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      toaster: Toaster
    ) = Ui<DialogsScreen> {
      EsScaffold(topBar = { EsAppBar { Text("Dialogs") } }) {
        EsLazyColumn(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          item {
            val items = listOf(1, 2, 3, 4, 5)
            var selected by remember { mutableIntStateOf(1) }
            Button(
              onClick = scopedAction {
                navigator.push(
                  BottomSheetScreen {
                    items.forEach { item ->
                      RadioListItem(
                        value = item == selected,
                        onValueChange = {
                          selected = item
                          dismiss()
                        },
                        headlineContent = { Text(item.toString()) }
                      )
                    }
                  }
                )
              }
            ) {
              Text("Single choice")
            }
          }
          item {
            val items = listOf("A", "B", "C")
            var selected by remember { mutableStateOf(items.toSet()) }
            Button(
              onClick = action {
                navigator.push(
                  BottomSheetScreen {
                    items.forEach { item ->
                      CheckboxListItem(
                        value = item in selected,
                        onValueChange = {
                          if (it) selected += item
                          else selected -= item
                        },
                        headlineContent = { Text(item) }
                      )
                    }
                  }
                )
              }
            ) { Text("Multi choice") }
          }
          item {
            val primaryColor = MaterialTheme.colorScheme.primary
            var currentColor by remember { mutableStateOf(primaryColor) }
            Button(
              onClick = action {
                navigator.push(
                  ColorPickerScreen(initialColor = currentColor)
                )?.let { currentColor = it }
              }
            ) { Text("Color choice") }
          }
          item {
            var current by remember { mutableStateOf("") }
            Button(
              onClick = action {
                navigator.push(
                  TextInputScreen(
                    label = "text...",
                    initial = current
                  )
                )?.let { current = it }
              }
            ) { Text("Text") }
          }
        }
      }
    }
  }
}
