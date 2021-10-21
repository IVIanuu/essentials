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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.colorpicker.ColorPickerDialog
import com.ivianuu.essentials.ui.LocalUiComponent
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.dialog.TextInputDialog
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.entryPoint
import kotlinx.coroutines.launch

@Provide val dialogsHomeItem = HomeItem("Dialogs") { DialogsKey }

object DialogsKey : Key<Unit>

@Provide val dialogsUi: KeyUi<DialogsKey> = {
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
  text: String
) {
  val component = entryPoint<DialogLauncherComponent>(LocalUiComponent.current)
  val scope = rememberCoroutineScope()
  TextButton(
    enabled = enabled,
    onClick = {
      onClick()
      scope.launch { component.navigator.popTop() }
    }
  ) {
    Text(text)
  }
}

@Composable private fun DialogLauncherButton(
  text: String,
  dismissible: Boolean = true,
  dialog: @Composable (() -> Unit) -> Unit
) {
  Spacer(Modifier.height(8.dp))

  val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current!!
  val component = entryPoint<DialogLauncherComponent>(LocalUiComponent.current)
  val scope = rememberCoroutineScope()
  Button(
    onClick = {
      scope.launch {
        component.navigator.push(
          DialogLauncherKey(dismissible) {
            dialog {
              if (dismissible) {
                onBackPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
              }
            }
          }
        )
      }
    }
  ) { Text(text) }
}

data class DialogLauncherKey(
  val dismissible: Boolean = true,
  val dialog: @Composable () -> Unit
) : DialogKey<Unit>

@Provide fun dialogLauncherUi(key: DialogLauncherKey): KeyUi<DialogLauncherKey> = {
  DialogScaffold(dismissible = key.dismissible) { key.dialog() }
}

@EntryPoint<UiComponent> interface DialogLauncherComponent {
  val navigator: Navigator
}
