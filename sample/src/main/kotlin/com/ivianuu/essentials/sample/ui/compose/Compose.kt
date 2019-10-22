package com.ivianuu.essentials.sample.ui.compose

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.LayoutSize
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.Prefs
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.Checkbox
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean

val composeRoute = composeControllerRoute {
    MaterialTheme {
        composable(key = "Test") {
            Column(
                mainAxisSize = LayoutSize.Expand,
                crossAxisSize = LayoutSize.Expand,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                composable("AppBar") {
                    Surface(elevation = 4.dp) {
                        TopAppBar(title = { Text("Compose") })
                    }
                }

                Prefs {
                    ListItem(
                        text = { Text("Hello") },
                        secondaryText = { Text("What's up") },
                        trailing = {
                            Checkbox(pref = (+inject<KPrefs>()).boolean("dummy_compose"))
                        }
                    )
                }
            }
        }
    }
}