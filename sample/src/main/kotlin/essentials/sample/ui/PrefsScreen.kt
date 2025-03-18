/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

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
import injekt.*
import kotlinx.serialization.*

@Provide val prefsHomeItem = HomeItem("Prefs") { PrefsScreen() }

class PrefsScreen : Screen<Unit>

@Provide @Composable fun PrefsUi(
  pref: DataStore<SamplePrefs>,
  context: ScreenContext<PrefsScreen> = inject,
): Ui<PrefsScreen> {
  val prefs by pref.data.collectAsScopedState(SamplePrefs())
  EsScaffold(topBar = { EsAppBar { Text("Prefs") } }) {
    EsLazyColumn {
      item {
        SectionSwitch(
          checked = prefs.switch,
          onCheckedChange = action { value ->
            pref.updateData { it.copy(switch = value) }
          },
          sectionType = SectionType.SINGLE,
          title = { Text("Switch") }
        )
      }

      item {
        SectionHeader(modifier = Modifier.interactive(prefs.switch)) { Text("Category") }
      }

      item {
        SectionSlider(
          sectionType = SectionType.FIRST,
          value = prefs.slider,
          onValueChangeFinished = action { value ->
            pref.updateData { it.copy(slider = value) }
          },
          modifier = Modifier.interactive(prefs.switch),
          headlineContent = { Text("Slider") },
          valueRange = 0..100,
          trailingContent = { Text(it.toString()) }
        )
      }

      item {
        SectionSlider(
          sectionType = SectionType.LAST,
          value = prefs.steppedSlider,
          onValueChangeFinished = action { value ->
            pref.updateData { it.copy(steppedSlider = value) }
          },
          modifier = Modifier.interactive(prefs.switch),
          headlineContent = { Text("Stepped slider") },
          stepPolicy = incrementingStepPolicy(0.05f),
          valueRange = 0.75f..1.5f,
          trailingContent = { ScaledPercentageUnitText(it) }
        )
      }

      item {
        SectionHeader(modifier = Modifier.interactive(prefs.switch)) {
          Text("Dialogs")
        }
      }

      item {
        SectionListItem(
          sectionType = SectionType.FIRST,
          modifier = Modifier.interactive(prefs.switch),
          onClick = scopedAction {
            val newTextInput = navigator().push(
              TextInputScreen(
                initial = prefs.textInput,
                label = "Input",
                predicate = { it.isNotEmpty() }
              )
            ) ?: return@scopedAction
            pref.updateData { it.copy(textInput = newTextInput) }
          },
          title = { Text("Text input") },
          description = { Text("This is a text input preference") }
        )
      }

      item {
        SectionListItem(
          sectionType = SectionType.LAST,
          onClick = action {
            val newColor = navigator().push(
              ColorPickerScreen(
                initialColor = prefs.color,
                title = "Color picker"
              )
            ) ?: return@action
            pref.updateData { it.copy(color = newColor) }
          },
          modifier = Modifier.interactive(prefs.switch),
          title = { Text("Color") },
          description = { Text("This is a color preference") },
          leading = { ColorIcon(prefs.color) }
        )
      }

      item {
        val items = listOf(1, 2, 3, 4, 5)
        var selected by remember { mutableIntStateOf(1) }
        Button(
          onClick = scopedAction {
            navigator().push(
              SingleChoiceListScreen(
                title = "Single choice",
                items = items,
                selected = selected
              ) { it.toString() }
            )
              ?.let { selected = it }
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
            navigator().push(
              MultiChoiceListScreen(
                title = "Multi choice",
                items = items,
                selected = selected
              )
            )?.let { selected = it }
          }
        ) { Text("Multi choice") }
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
