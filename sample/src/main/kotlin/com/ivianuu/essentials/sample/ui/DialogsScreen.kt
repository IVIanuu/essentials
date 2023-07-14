/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.dialog.ColorPickerDialog
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.dialog.DialogScreen
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.dialog.TextInputDialog
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

@Provide val dialogsHomeItem = HomeItem("Dialogs") { DialogsScreen() }

class DialogsScreen : Screen<Unit>

@Provide fun dialogsUi(@Inject navigator: Navigator) = Ui<DialogsScreen, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Dialogs") }) }
  ) {
    VerticalList(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      item {
        DialogLauncherButton(text = "Simple") {
          Dialog(
            title = { Text("Simple") },
            content = { Text("This is a message") },
            buttons = {
              DialogCloseButton(text = "Cancel")
              DialogCloseButton(text = "OK")
            }
          )
        }
      }
      item {
        DialogLauncherButton(
          text = "Simple with neutral"
        ) {
          Dialog(
            title = { Text("Simple") },
            content = { Text("This is a message") },
            buttons = {
              DialogCloseButton(text = "Neutral")
              DialogCloseButton(text = "Negative")
              DialogCloseButton(text = "Positive")
            }
          )
        }
      }
      item {
        DialogLauncherButton(text = "Title only") {
          Dialog(
            title = { Text("Title only") }
          )
        }
      }
      item {
        DialogLauncherButton(text = "With icon") {
          Dialog(
            title = { Text("With icon") },
            icon = { Icon(Icons.Default.Settings) },
            buttons = {
              DialogCloseButton(text = "OK")
            }
          )
        }
      }
      item {
        DialogLauncherButton(text = "Message only") {
          Dialog(content = { Text("Message only") })
        }
      }
      item {
        DialogLauncherButton(text = "Buttons only") {
          Dialog(
            buttons = {
              DialogCloseButton(text = "Cancel")
              DialogCloseButton(text = "OK")
            }
          )
        }
      }
      item {
        DialogLauncherButton(
          dismissible = false,
          text = "Not cancelable"
        ) {
          Dialog(
            title = { Text("Not cancelable") },
            buttons = {
              DialogCloseButton(text = "Close")
            }
          )
        }
      }
      item {
        DialogLauncherButton(text = "List") {
          Dialog(
            title = { Text("List") },
            content = {
              LazyColumn {
                items((1..100).toList()) { item ->
                  ListItem(
                    title = {
                      Text(
                        "Item: $item"
                      )
                    }
                  )
                }
              }
            },
            applyContentPadding = false,
            buttons = {
              DialogCloseButton(text = "Close")
            }
          )
        }
      }
      item {
        val singleChoiceItems = listOf(1, 2, 3, 4, 5)
        var selectedSingleChoiceItem by remember { mutableStateOf(1) }
        DialogLauncherButton(text = "Single choice list") {
          var tmpSelectedItem by remember { mutableStateOf(selectedSingleChoiceItem) }

          SingleChoiceListDialog(
            title = { Text("Single choice") },
            items = singleChoiceItems,
            selectedItem = tmpSelectedItem,
            onSelectionChanged = { tmpSelectedItem = it },

            item = { Text("Item: $it") },
            buttons = {
              DialogCloseButton(text = "Cancel")
              DialogCloseButton(
                text = "OK",
                onClick = { selectedSingleChoiceItem = tmpSelectedItem }
              )
            }
          )
        }
      }
      item {
        val multiChoiceItems = listOf("A", "B", "C")
        var selectedMultiChoiceItems by remember { mutableStateOf(multiChoiceItems.toSet()) }
        DialogLauncherButton(text = "Multi choice list") {
          var tmpSelectedItems by remember { mutableStateOf(selectedMultiChoiceItems) }

          MultiChoiceListDialog(
            title = { Text("Multi choice") },
            items = multiChoiceItems,
            selectedItems = tmpSelectedItems,
            onSelectionsChanged = { tmpSelectedItems = it },
            item = { Text(it) },
            buttons = {
              DialogCloseButton(text = "Cancel")
              DialogCloseButton(
                text = "OK",
                onClick = { selectedMultiChoiceItems = tmpSelectedItems }
              )
            }
          )
        }
      }
      item {
        val primaryColor = MaterialTheme.colors.primary
        var currentColor by remember { mutableStateOf(primaryColor) }
        DialogLauncherButton(text = "Color Picker") { dismiss ->
          ColorPickerDialog(
            title = { Text("Color Picker") },
            showAlphaSelector = true,
            initialColor = currentColor,
            onCancel = dismiss,
            onColorSelected = {
              currentColor = it
              dismiss()
            }
          )
        }
      }
      item {
        var textInputValue by remember { mutableStateOf("") }
        DialogLauncherButton(text = "Text input") {
          var tmpTextInputValue by remember { mutableStateOf(textInputValue) }
          TextInputDialog(
            value = tmpTextInputValue,
            onValueChange = { tmpTextInputValue = it },
            title = { Text("Text input") },
            label = { Text("Hint..") },
            buttons = {
              DialogCloseButton(text = "Cancel")
              DialogCloseButton(
                text = "OK",
                onClick = { textInputValue = tmpTextInputValue },
                enabled = tmpTextInputValue.isNotEmpty()
              )
            }
          )
        }
      }
    }
  }
}

@Composable private fun DialogCloseButton(
  enabled: Boolean = true,
  onClick: () -> Unit = {},
  text: String,
  @Inject navigator: Navigator
) {
  TextButton(
    enabled = enabled,
    onClick = action {
      onClick()
      navigator.popTop()
    }
  ) {
    Text(text)
  }
}

@Composable private fun DialogLauncherButton(
  text: String,
  dismissible: Boolean = true,
  @Inject navigator: Navigator,
  dialog: @Composable (() -> Unit) -> Unit
) {
  Spacer(Modifier.height(8.dp))

  val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current!!
  Button(
    onClick = action {
      navigator.push(
        DialogLauncherScreen(dismissible) {
          dialog {
            if (dismissible)
              onBackPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
          }
        }
      )
    }
  ) { Text(text) }
}

class DialogLauncherScreen(
  val dismissible: Boolean = true,
  val dialog: @Composable () -> Unit
) : DialogScreen<Unit>

@Provide fun dialogLauncherUi(screen: DialogLauncherScreen) = Ui<DialogLauncherScreen, Unit> {
  DialogScaffold(dismissible = screen.dismissible) { screen.dialog() }
}
