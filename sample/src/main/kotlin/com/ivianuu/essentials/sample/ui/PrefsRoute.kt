package com.ivianuu.essentials.sample.ui

import com.ivianuu.compose.common.RecyclerView
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.changehandler.FadeChangeHandler
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.CheckBox
import com.ivianuu.essentials.ui.compose.CheckBoxPreference
import com.ivianuu.essentials.ui.compose.ListItem
import com.ivianuu.essentials.ui.compose.RadioButton
import com.ivianuu.essentials.ui.compose.RadioButtonPreference
import com.ivianuu.essentials.ui.compose.Scaffold
import com.ivianuu.essentials.ui.compose.Switch
import com.ivianuu.essentials.ui.compose.SwitchPreference
import com.ivianuu.essentials.ui.compose.Text
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.showMultiSelectDialogOnClick
import com.ivianuu.essentials.ui.compose.showTextInputDialogOnClick
import com.ivianuu.essentials.ui.prefs.Prefs
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean
import com.ivianuu.kprefs.string
import com.ivianuu.kprefs.stringSet

fun PrefsRoute() = Route(handler = FadeChangeHandler()) {
    Scaffold(
        appBar = { AppBar(title = "Prefs") },
        content = {
            Prefs {
                RecyclerView {
                    val prefs = inject<KPrefs>()

                    SwitchPreference(
                        title = { Text("Switch") },
                        text = { Text("This is a switch") },
                        pref = prefs.boolean("switch")
                    )

                    CheckBoxPreference(
                        title = { Text("Checkbox") },
                        text = { Text("This is a checkbox") },
                        pref = prefs.boolean("checkbox")
                    )

                    RadioButtonPreference(
                        title = { Text("Radio button") },
                        text = { Text("This is a radio button") },
                        pref = prefs.boolean("radio")
                    )

                    ListItem(
                        title = { Text("Text input") },
                        text = { Text("Text input is awesome") },
                        onClick = showTextInputDialogOnClick(
                            title = "Text input sample",
                            pref = prefs.string("text_input")
                        )
                    )

                    ListItem(
                        title = { Text("Multi selection") },
                        text = { Text("Multi selectionZz") },
                        onClick = showMultiSelectDialogOnClick(
                            title = "Text input sample",
                            pref = prefs.stringSet("text_input", setOf("B")),
                            items = arrayOf(
                                "A" to "A",
                                "B" to "B",
                                "C" to "C"
                            )
                        )
                    )
                }
            }
        }
    )
}