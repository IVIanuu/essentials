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
          sectionType = SectionType.FIRST_WITH_HEADER,
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
        SectionListItem(
          sectionType = SectionType.SINGLE,
          modifier = Modifier.interactive(prefs.switch),
          onClick = scopedAction {
            navigator().push(
              BottomSheetScreen {
                TextInputUi(
                  initial = prefs.textInput,
                  label = "Input",
                  predicate = { it.isNotEmpty() },
                  onResult = scopedAction { value ->
                    pref.updateData { it.copy(textInput = value ?: "") }
                  }
                )
              }
            )
          },
          title = { Text("Text input") },
          description = { Text("This is a text input preference") }
        )
      }

      item {
        SectionHeader(modifier = Modifier.interactive(prefs.switch)) {
          Text("Colors")
        }
      }

      item {
        SectionSwitch(
          sectionType = SectionType.FIRST_WITH_HEADER,
          modifier = Modifier.interactive(prefs.switch),
          title = { Text("Material you") },
          checked = prefs.materialYou,
          onCheckedChange = action { value -> pref.updateData { it.copy(materialYou = value) } }
        )
      }

      item {
        SectionListItem(
          sectionType = SectionType.MIDDLE,
          onClick = action {
            val newColor = navigator().push(
              ColorPickerScreen(
                initialColor = prefs.primary,
                title = "Primary color"
              )
            ) ?: return@action
            pref.updateData { it.copy(primary = newColor) }
          },
          modifier = Modifier.interactive(prefs.switch),
          title = { Text("Primary color") },
          description = { Text("This is a color preference") },
          trailing = { ColorIcon(prefs.primary) }
        )
      }

      item {
        SectionListItem(
          sectionType = SectionType.MIDDLE,
          onClick = action {
            val newColor = navigator().push(
              ColorPickerScreen(
                initialColor = prefs.secondary,
                title = "Secondary color"
              )
            ) ?: return@action
            pref.updateData { it.copy(secondary = newColor) }
          },
          modifier = Modifier.interactive(prefs.switch),
          title = { Text("Secondary color") },
          description = { Text("This is a color preference") },
          trailing = { ColorIcon(prefs.secondary) }
        )
      }

      item {
        SectionListItem(
          sectionType = SectionType.LAST,
          onClick = action {
            val newColor = navigator().push(
              ColorPickerScreen(
                initialColor = prefs.tertiary,
                title = "Tertiary color"
              )
            ) ?: return@action
            pref.updateData { it.copy(tertiary = newColor) }
          },
          modifier = Modifier.interactive(prefs.switch),
          title = { Text("Tertiary color") },
          description = { Text("This is a color preference") },
          trailing = { ColorIcon(prefs.tertiary) }
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
  val materialYou: Boolean = true,
  @Contextual val primary: Color = Color.Red,
  @Contextual val secondary: Color = Color.Blue,
  @Contextual val tertiary: Color = Color.Green,
  val multiChoice: Set<String> = setOf("A", "B", "C"),
  val singleChoice: String = "C",
) {
  @Provide companion object {
    @Provide val dataStoreProvider = DataStoreProvider("sample_prefs") { SamplePrefs() }
  }
}
