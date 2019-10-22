package com.ivianuu.essentials.sample.ui.compose

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.LayoutSize
import androidx.ui.material.*
import androidx.ui.material.surface.Surface
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.retainedState
import com.ivianuu.essentials.util.StringProvider

val composeRoute = composeControllerRoute {
    MaterialTheme {
        composable(key = "Test") {
            Column(
                mainAxisSize = LayoutSize.Expand,
                crossAxisSize = LayoutSize.Expand,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                val stringProvider = +inject<StringProvider>()
                Surface(elevation = 4.dp) {
                    TopAppBar(title = { Text(text = stringProvider.getString(R.string.about_title)) })
                }

                VerticalScroller {
                    Column {
                        (1..1).forEach { i ->
                            composable(i) {
                                val state = +retainedState(i) { false }

                                ListItem(
                                    text = { Text("Hello $i") },
                                    secondaryText = { Text("What's up") },
                                    trailing = {
                                        d { "composing" }
                                        Checkbox(
                                            checked = state.value,
                                            onCheckedChange = { state.value = it })
                                    }
                                )

                                if (i != 20) {
                                    Divider(color = Color.Black.copy(alpha = 0.12f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}