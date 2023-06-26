/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.colorpicker.ColorPickerScreen
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.PrefModule
import com.ivianuu.essentials.ui.common.IconPlaceholder
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.dialog.MultiChoiceListScreen
import com.ivianuu.essentials.ui.dialog.SingleChoiceListScreen
import com.ivianuu.essentials.ui.dialog.TextInputScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.incrementingStepPolicy
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.prefs.ColorListItem
import com.ivianuu.essentials.ui.prefs.RadioButtonListItem
import com.ivianuu.essentials.ui.prefs.ScaledPercentageUnitText
import com.ivianuu.essentials.ui.prefs.SliderListItem
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Provide val prefsHomeItem = HomeItem("Prefs") { PrefsScreen() }

class PrefsScreen : Screen<Unit>

@Provide fun prefsUi(
  navigator: Navigator,
  pref: DataStore<SamplePrefs>
) = Ui<PrefsScreen, Unit> {
  val prefs by pref.data.collectAsState(remember { SamplePrefs() })
  SimpleListScreen("Prefs") {
    item {
      SwitchListItem(
        value = prefs.switch,
        onValueChange = action { value ->
          pref.updateData { copy(switch = value) }
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
        onValueChange = action { value ->
          pref.updateData { copy(radioButton = value) }
        },
        modifier = Modifier.interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Radio Button") },
        subtitle = { Text("This is a radio button preference") }
      )
    }
    item {
      SliderListItem(
        value = prefs.slider,
        onValueChangeFinished = action { value ->
          pref.updateData { copy(slider = value) }
        },
        modifier = Modifier.interactive(prefs.switch),
        leading = { Icon(Icons.Default.ThumbUp) },
        title = { Text("Slider") },
        valueRange = 0..100,
        valueText = { Text(it.toString()) }
      )
    }
    item {
      SliderListItem(
        value = prefs.slider,
        onValueChangeFinished = action { value ->
          pref.updateData { copy(slider = value) }
        },
        modifier = Modifier.interactive(prefs.switch),
        leading = { Icon(Icons.Default.ThumbUp) },
        title = { Text("Slider") },
        valueRange = 0..100,
        valueText = { Text(it.toString()) }
      )
    }
    item {
      SliderListItem(
        value = prefs.steppedSlider,
        onValueChangeFinished = action { value ->
          pref.updateData { copy(steppedSlider = value) }
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
          .clickable(
            onClick = action {
              val newTextInput = navigator.push(
                TextInputScreen(
                  initial = prefs.textInput,
                  label = "Input",
                  title = "Text input",
                  predicate = { it.isNotEmpty() }
                )
              ) ?: return@action
              pref.updateData { copy(textInput = newTextInput) }
            }
          )
          .interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Text input") },
        subtitle = { Text("This is a text input preference") }
      )
    }
    item {
      ColorListItem(
        value = prefs.color,
        onValueChangeRequest = action {
          val newColor = navigator.push(
            ColorPickerScreen(initialColor = prefs.color)
          ) ?: return@action
          pref.updateData { copy(color = newColor) }
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
          .clickable(onClick = action {
            val newItems = navigator.push(
              MultiChoiceListScreen(
                items = listOf("A", "B", "C"),
                selectedItems = prefs.multiChoice
              )
            ) ?: return@action
            pref.updateData { copy(multiChoice = newItems) }
          })
          .interactive(prefs.switch),
        leading = { IconPlaceholder() },
        title = { Text("Multi select list") },
        subtitle = { Text("This is a multi select list preference") }
      )
    }
    item {
      ListItem(
        modifier = Modifier
          .clickable(onClick = action {
            val newItem = navigator.push(
              SingleChoiceListScreen(
                items = listOf("A", "B", "C"),
                selectedItem = prefs.singleChoice
              )
            ) ?: return@action
            pref.updateData { copy(singleChoice = newItem) }
          })
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
  @Contextual val color: Color = Color.Red,
  val multiChoice: Set<String> = setOf("A", "B", "C"),
  val singleChoice: String = "C",
) {
  companion object {
    @Provide val prefModule = PrefModule { SamplePrefs() }
  }
}
