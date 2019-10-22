package com.ivianuu.essentials.sample.ui.compose

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import com.ivianuu.essentials.ui.compose.Prefs
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.Checkbox
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean

val composeRoute = composeControllerRoute {
    MaterialTheme {
        composable(key = "Test") {
            Scaffold(
                appBar = { TopAppBar(title = { Text("Compose") }) },
                content = {
                    VerticalScroller {
                        Column {
                            Prefs {
                                (0..100).forEach { i ->
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
                        }
                    }
                }
            )
        }
    }
}