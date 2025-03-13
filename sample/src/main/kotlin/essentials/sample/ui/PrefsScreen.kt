/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.datastore.core.*
import essentials.compose.*
import essentials.data.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.overlay.*
import essentials.ui.prefs.*
import injekt.*
import kotlinx.serialization.*

@Provide val prefsHomeItem = HomeItem("Prefs") { PrefsScreen() }

class PrefsScreen : Screen<Unit>

@Provide @Composable fun PrefsUi(
  navigator: Navigator,
  pref: DataStore<SamplePrefs>
): Ui<PrefsScreen> {
  val prefs by pref.data.collectAsScopedState(SamplePrefs())
  EsScaffold(topBar = { EsAppBar { Text("Prefs") } }) {
    EsLazyColumn {
      item {
        SwitchListItem(
          value = prefs.switch,
          onValueChange = action { value ->
            pref.updateData { it.copy(switch = value) }
          },
          leadingContent = { Icon(Icons.Default.ThumbUp, null) },
          headlineContent = { Text("Switch") }
        )
      }

      item {
        Subheader(modifier = Modifier.interactive(prefs.switch)) { Text("Category") }
      }

      item {
        SliderListItem(
          value = prefs.slider,
          onValueChangeFinished = action { value ->
            pref.updateData { it.copy(slider = value) }
          },
          modifier = Modifier.interactive(prefs.switch),
          leadingContent = { Icon(Icons.Default.ThumbUp, null) },
          headlineContent = { Text("Slider") },
          valueRange = 0..100,
          trailingContent = { Text(it.toString()) }
        )
      }

      item {
        SliderListItem(
          value = prefs.slider,
          onValueChangeFinished = action { value ->
            pref.updateData { it.copy(slider = value) }
          },
          modifier = Modifier.interactive(prefs.switch),
          leadingContent = { Icon(Icons.Default.ThumbUp, null) },
          headlineContent = { Text("Slider") },
          valueRange = 0..100,
          trailingContent = { Text(it.toString()) }
        )
      }

      item {
        SliderListItem(
          value = prefs.steppedSlider,
          onValueChangeFinished = action { value ->
            pref.updateData { it.copy(steppedSlider = value) }
          },
          modifier = Modifier.interactive(prefs.switch),
          leadingContent = { Icon(Icons.Default.ThumbUp, null) },
          headlineContent = { Text("Stepped slider") },
          stepPolicy = incrementingStepPolicy(0.05f),
          valueRange = 0.75f..1.5f,
          trailingContent = { ScaledPercentageUnitText(it) }
        )
      }

      item {
        Subheader(modifier = Modifier.interactive(prefs.switch)) {
          Text("Dialogs")
        }
      }

      item {
        EsListItem(
          modifier = Modifier.interactive(prefs.switch),
          onClick = scopedAction {
            val newTextInput = navigator.push(
              TextInputScreen(
                initial = prefs.textInput,
                label = "Input",
                predicate = { it.isNotEmpty() }
              )
            ) ?: return@scopedAction
            pref.updateData { it.copy(textInput = newTextInput) }
          },
          leadingContent = { Icon(Icons.Default.ThumbUp, null) },
          headlineContent = { Text("Text input") },
          supportingContent = { Text("This is a text input preference") }
        )
      }

      item {
        ColorListItem(
          value = prefs.color,
          onValueChangeRequest = action {
            val newColor = navigator.push(
              ColorPickerScreen(initialColor = prefs.color)
            ) ?: return@action
            pref.updateData { it.copy(color = newColor) }
          },
          modifier = Modifier.interactive(prefs.switch),
          leadingContent = { Icon(Icons.Default.ThumbUp, null) },
          headlineContent = { Text("Color") },
          supportingContent = { Text("This is a color preference") }
        )
      }
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
  @Provide companion object {
    @Provide val dataStoreProvider = DataStoreProvider("sample_prefs") { SamplePrefs() }
  }
}
