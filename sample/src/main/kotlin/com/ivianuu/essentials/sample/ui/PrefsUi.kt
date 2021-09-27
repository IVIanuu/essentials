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

import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.colorpicker.ColorPickerKey
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.dialog.MultiChoiceListKey
import com.ivianuu.essentials.ui.dialog.SingleChoiceListKey
import com.ivianuu.essentials.ui.dialog.TextInputKey
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.incrementingStepPolicy
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.prefs.ColorListItem
import com.ivianuu.essentials.ui.prefs.FloatSliderListItem
import com.ivianuu.essentials.ui.prefs.IntSliderListItem
import com.ivianuu.essentials.ui.prefs.RadioButtonListItem
import com.ivianuu.essentials.ui.prefs.ScaledPercentageUnitText
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Provide val prefsHomeItem = HomeItem("Prefs") { PrefsKey }

object PrefsKey : Key<Unit>

@Provide fun prefsUi(
  navigator: Navigator,
  prefStore: DataStore<SamplePrefs>,
  scope: NamedCoroutineScope<KeyUiScope>
): KeyUi<PrefsKey> = {
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
        modifier = Modifier.interactive(prefs.switch),
        title = { Text("Text input") },
        subtitle = { Text("This is a text input preference") },
        onClick = {
          scope.launch {
            val newTextInput = navigator.push(
              TextInputKey(
                initial = prefs.textInput,
                label = "Input",
                title = "Text input",
                allowEmpty = false
              )
            ) ?: return@launch
            scope.launch {
              prefStore.updateData { copy(textInput = newTextInput) }
            }
          }
        }
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
            scope.launch {
              prefStore.updateData { copy(color = newColor.toArgb()) }
            }
          }
        },
        modifier = Modifier.interactive(prefs.switch),
        title = { Text("Color") },
        subtitle = { Text("This is a color preference") }
      )
    }
    item {
      ListItem(
        modifier = Modifier.interactive(prefs.switch),
        title = { Text("Multi select list") },
        subtitle = { Text("This is a multi select list preference") },
        onClick = {
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
            scope.launch {
              prefStore.updateData { copy(multiChoice = newItems) }
            }
          }
        }
      )
    }
    item {
      ListItem(
        modifier = Modifier.interactive(prefs.switch),
        title = { Text("Single item list") },
        subtitle = { Text("This is a single item list preference") },
        onClick = {
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
            scope.launch {
              prefStore.updateData { copy(singleChoice = newItem) }
            }
          }
        }
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
    @Provide val prefModule = PrefModule("sample_prefs") { SamplePrefs() }
  }
}
