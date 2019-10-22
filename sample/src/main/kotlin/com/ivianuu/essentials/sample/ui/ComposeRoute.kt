package com.ivianuu.essentials.sample.ui

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.material.ListItem
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Subheader
import com.ivianuu.essentials.ui.compose.prefs.Checkbox
import com.ivianuu.essentials.ui.compose.prefs.PrefsScreen
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean

val composeRoute = composeControllerRoute {
    PrefsScreen(
        appBar = { EsTopAppBar(title = "Compose") },
        prefs = {
            (0..20).forEach { i ->
                if (i == 5 || i == 10 || i == 15) {
                    Subheader("Header: $i")
                }

                val pref = (+inject<KPrefs>()).boolean("dummy_compose_$i")
                composable(key = i, inputs = arrayOf(pref.get())) {
                    ListItem(
                        text = { Text("Hello $i") },
                        secondaryText = { Text("What's up $i") },
                        trailing = { Checkbox(pref = pref) },
                        onClick = { pref.set(!pref.get()) }
                    )
                }
            }
        }
    )
}