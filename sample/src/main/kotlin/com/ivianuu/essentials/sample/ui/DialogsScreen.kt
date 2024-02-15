/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.dialog.ColorPickerScreen
import com.ivianuu.essentials.ui.dialog.ConfirmationScreen
import com.ivianuu.essentials.ui.dialog.ListScreen
import com.ivianuu.essentials.ui.dialog.MultiChoiceListScreen
import com.ivianuu.essentials.ui.dialog.SingleChoiceListScreen
import com.ivianuu.essentials.ui.dialog.TextInputScreen
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

@Provide val dialogsHomeItem = HomeItem("Dialogs") { DialogsScreen() }

class DialogsScreen : Screen<Unit>

@Provide fun dialogsUi(
  navigator: Navigator,
  toaster: Toaster
) = Ui<DialogsScreen, Unit> {
  ScreenScaffold(topBar = { AppBar { Text("Dialogs") } }) {
    VerticalList(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      item {
        Button(
          onClick = action {
            navigator.push(
              ListScreen(
                (1..100).toList()
              ) { it.toString() }
            )
          }
        ) { Text("List") }
      }
      item {
        val items = listOf(1, 2, 3, 4, 5)
        var selected by remember { mutableStateOf(1) }
        Button(
          onClick = action {
            navigator.push(
              SingleChoiceListScreen(
                items = items,
                selected = selected
              ) { it.toString() }
            )?.let { selected = it }
          }
        ) { Text("Single choice") }
      }
      item {
        val items = listOf("A", "B", "C")
        var selected by remember { mutableStateOf(items.toSet()) }
        Button(
          onClick = action {
            navigator.push(
              MultiChoiceListScreen(
                items = items,
                selected = selected
              )
            )?.let { selected = it }
          }
        ) { Text("Multi choice") }
      }
      item {
        val primaryColor = MaterialTheme.colors.primary
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
              TextInputScreen(initial = current)
            )?.let { current = it }
          }
        ) { Text("Text") }
      }
      item {
        Button(
          onClick = action {
            navigator.push(
              ConfirmationScreen(title = "Would you like to share your private data?")
            )?.let { toaster("result = $it") }
          }
        ) { Text("Confirmation") }
      }
    }
  }
}
