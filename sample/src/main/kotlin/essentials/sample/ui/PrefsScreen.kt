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
  pref: DataStore<SamplePrefs>,
  context: ScreenContext<PrefsScreen> = inject,
): Ui<PrefsScreen> {
  val prefs by pref.data.collectAsScopedState(SamplePrefs())
  EsScaffold(topBar = { EsAppBar { Text("Prefs") } }) {
    EsLazyColumn {
      item {
        SwitchListItem(
          value = prefs.switch,
          sectionType = SectionType.SINGLE,
          onValueChange = action { value ->
            pref.updateData { it.copy(switch = value) }
          },
          leadingContent = { Icon(Icons.Default.ThumbUp, null) },
          headlineContent = { Text("Switch") }
        )
      }

      item {
        SectionHeader(modifier = Modifier.interactive(prefs.switch)) { Text("Category") }
      }

      item {
        SliderListItem(
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
        SliderListItem(
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
          headlineContent = { Text("Text input") },
          supportingContent = { Text("This is a text input preference") }
        )
      }

      item {
        ColorListItem(
          sectionType = SectionType.LAST,
          value = prefs.color,
          onValueChangeRequest = action {
            val newColor = navigator().push(
              ColorPickerScreen(initialColor = prefs.color)
            ) ?: return@action
            pref.updateData { it.copy(color = newColor) }
          },
          modifier = Modifier.interactive(prefs.switch),
          headlineContent = { Text("Color") },
          supportingContent = { Text("This is a color preference") }
        )
      }

      item {
        val items = listOf(1, 2, 3, 4, 5)
        var selected by remember { mutableIntStateOf(1) }
        Button(
          onClick = scopedAction {
            navigator().push(
              SingleChoiceListScreen(
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

      item {
        val primaryColor = MaterialTheme.colorScheme.primary
        var currentColor by remember { mutableStateOf(primaryColor) }
        Button(
          onClick = action {
            navigator().push(
              ColorPickerScreen(initialColor = currentColor)
            )?.let { currentColor = it }
          }
        ) { Text("Color choice") }
      }

      item {
        var current by remember { mutableStateOf("") }
        Button(
          onClick = action {
            navigator().push(
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
