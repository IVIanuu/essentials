/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.datastore.core.*
import com.materialkolor.*
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
      singleItemSection {
        SectionSwitch(
          checked = prefs.switch,
          onCheckedChange = action { value ->
            pref.updateData { it.copy(switch = value) }
          },
          title = { Text("Switch") }
        )
      }

      section(header = {
        SectionHeader(modifier = Modifier.interactive(prefs.switch)) { Text("Category") }
      }) {
        item {
          SectionSlider(
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

        singleItemSection {
          SectionListItem(
            modifier = Modifier.interactive(prefs.switch),
            onClick = scopedAction {
              navigator().push(
                TextInputScreen(
                  initial = prefs.textInput,
                  label = "Input",
                  predicate = { it.isNotEmpty() }
                )
              )?.let { value ->
                pref.updateData { it.copy(textInput = value) }
              }
            },
            title = { Text("Text input") },
            description = { Text("This is a text input preference") }
          )
        }
      }

      section(header = {
        SectionHeader(modifier = Modifier.interactive(prefs.switch)) {
          Text("Colors")
        }
      }) {
        item {
          SectionSwitch(
            modifier = Modifier.interactive(prefs.switch),
            title = { Text("Material you") },
            checked = prefs.materialYou,
            onCheckedChange = action { value -> pref.updateData { it.copy(materialYou = value) } }
          )
        }

        item {
          SectionListItem(
            modifier = Modifier.interactive(prefs.switch),
            title = { Text("Palette style") },
            onClick = scopedAction {
              navigator().push(
                SingleChoiceListScreen(
                  title = "Palette style",
                  items = PaletteStyle.entries,
                  selected = prefs.paletteStyle
                ) { it.toString() }
              )
                ?.let { value -> pref.updateData { it.copy(paletteStyle = value) } }
            }
          )
        }

        item {
          SectionListItem(
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
  val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
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
