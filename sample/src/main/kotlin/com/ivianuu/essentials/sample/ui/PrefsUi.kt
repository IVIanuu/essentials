/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.colorpicker.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.serialization.*

@Provide val prefsHomeItem = HomeItem("Prefs") { PrefsKey }

object PrefsKey : Key<Unit>

@Provide fun prefsUi(
  prefStore: DataStore<SamplePrefs>,
  navigator: Navigator,
  scope: NamedCoroutineScope<KeyUiScope>
) = KeyUi<PrefsKey> {
  val prefs by prefStore.data.collectAsState(remember { SamplePrefs() })
  SimpleListScreen("Prefs") {
    item {
      SwitchListItem(
        value = prefs.switch,
        onValueChange = {
          scope.launch {
            prefStore.updateData { copy(switch = it) }
          }
        },
        leading = { IconPlaceholder() },
        title = { Text("Switch") }
      )
    }
    item {
      Subheader(modifier = Modifier.interactive(prefs.switch)) { Text("Category") }
    }
    item {
      RadioButtonListItem(
        value = prefs.radioButton,
        onValueChange = {
          scope.launch {
            prefStore.updateData { copy(radioButton = it) }
          }
        },
        modifier = Modifier.interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Radio Button") },
        subtitle = { Text("This is a radio button preference") }
      )
    }
    item {
      IntSliderListItem(
        value = prefs.slider,
        onValueChange = {
          scope.launch {
            prefStore.updateData { copy(slider = it) }
          }
        },
        modifier = Modifier.interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Slider") },
        subtitle = { Text("This is a slider preference") },
        valueRange = 0..100,
        valueText = { Text(it.toString()) }
      )
    }
    item {
      FloatSliderListItem(
        value = prefs.steppedSlider,
        onValueChange = {
          scope.launch {
            prefStore.updateData { copy(steppedSlider = it) }
          }
        },
        modifier = Modifier.interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Stepped slider") },
        subtitle = { Text("This is a stepped slider preference") },
        stepPolicy = incrementingStepPolicy(0.05f),
        valueRange = 0.75f..1.5f,
        valueText = { ScaledPercentageUnitText(it) }
      )
    }
    item {
      Subheader(modifier = Modifier.interactive(prefs.switch)) {
        Text("Dialogs")
      }
    }
    item {
      ListItem(
        modifier = Modifier
          .clickable {
            scope.launch {
              val newTextInput = navigator.push(
                TextInputKey(
                  initial = prefs.textInput,
                  label = "Input",
                  title = "Text input",
                  predicate = { it.isNotEmpty() }
                )
              ) ?: return@launch
              launch {
                prefStore.updateData { copy(textInput = newTextInput) }
              }
            }
          }
          .interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Text input") },
        subtitle = { Text("This is a text input preference") }
      )
    }
    item {
      ColorListItem(
        value = Color(prefs.color),
        onValueChangeRequest = {
          scope.launch {
            val newColor = navigator.push(
              ColorPickerKey(
                initialColor = Color(prefs.color),
                title = "Color"
              )
            ) ?: return@launch
            launch {
              prefStore.updateData { copy(color = newColor.toArgb()) }
            }
          }
        },
        modifier = Modifier.interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Color") },
        subtitle = { Text("This is a color preference") }
      )
    }
    item {
      ListItem(
        modifier = Modifier
          .clickable {
            scope.launch {
              val newItems = navigator.push(
                MultiChoiceListKey(
                  items = listOf(
                    MultiChoiceListKey.Item("A", "A"),
                    MultiChoiceListKey.Item("B", "B"),
                    MultiChoiceListKey.Item("C", "C")
                  ),
                  selectedItems = prefs.multiChoice,
                  title = "Multi select list"
                )
              ) ?: return@launch
              launch {
                prefStore.updateData { copy(multiChoice = newItems) }
              }
            }
          }
          .interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Multi select list") },
        subtitle = { Text("This is a multi select list preference") }
      )
    }
    item {
      ListItem(
        modifier = Modifier
          .clickable {
            scope.launch {
              val newItem = navigator.push(
                SingleChoiceListKey(
                  items = listOf(
                    SingleChoiceListKey.Item("A", "A"),
                    SingleChoiceListKey.Item("B", "B"),
                    SingleChoiceListKey.Item("C", "C")
                  ),
                  selectedItem = prefs.singleChoice,
                  title = "Single item list"
                )
              ) ?: return@launch
              launch {
                prefStore.updateData { copy(singleChoice = newItem) }
              }
            }
          }
          .interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Single item list") },
        subtitle = { Text("This is a single item list preference") }
      )
    }
  }
}

@Serializable data class SamplePrefs(
  val switch: Boolean = false,
  val radioButton: Boolean = false,
  val slider: Int = 50,
  val steppedSlider: Float = 0.5f,
  val textInput: String = "",
  val color: Int = Color.Red.toArgb(),
  val multiChoice: Set<String> = setOf("A", "B", "C"),
  val singleChoice: String = "C",
) {
  companion object {
    @Provide val prefModule = PrefModule { SamplePrefs() }
  }
}
