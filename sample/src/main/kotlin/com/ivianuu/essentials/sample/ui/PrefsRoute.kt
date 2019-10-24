package com.ivianuu.essentials.sample.ui

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Subheader
import com.ivianuu.essentials.ui.compose.prefs.CheckboxPreference
import com.ivianuu.essentials.ui.compose.prefs.PrefsScreen
import com.ivianuu.essentials.ui.compose.prefs.RadioButtonPreference
import com.ivianuu.essentials.ui.compose.prefs.SwitchPreference
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.vertical
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean

val prefsRoute = composeControllerRoute(
    options = controllerRouteOptions().vertical()
) {
    PrefsScreen(
        appBar = { EsTopAppBar(title = "Prefs") },
        prefs = {
            val prefs = +inject<KPrefs>()

            SwitchPreference(
                pref = prefs.boolean("switch"),
                title = { Text("Switch") }
            )

            Subheader("Category")

            CheckboxPreference(
                pref = prefs.boolean("checkbox"),
                title = { Text("Checkbox") },
                summary = { Text("This is a checkbox preference") }
            )

            RadioButtonPreference(
                pref = prefs.boolean("radio_button"),
                title = { Text("Radio Button") },
                summary = { Text("This is a radio button preference") }
            )
        }
    )
}