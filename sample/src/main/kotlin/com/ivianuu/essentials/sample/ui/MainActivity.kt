/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import android.os.Bundle
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.Align
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : EsActivity() {

    override val startRoute: ControllerRoute?
        get() = null//counterRoute(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                App()
            }
        }

        /*
        get<KPrefs>().boolean("tile_state").asFlow()
            .onEach { d { "tile state changed $it" } }
            .launchIn(lifecycleScope)

        get<BroadcastFactory>().create(Intent.ACTION_SCREEN_OFF)
            .onEach {
                lifecycleScope.launch {
                    delay(2000)
                    d { "unlock screen ${get<ScreenUnlocker>().unlockScreen()}" }
                }
            }
            .launchIn(lifecycleScope)*/
    }

    override fun onDestroy() {
        setContent { }
        super.onDestroy()
    }

}

@Composable
private fun App() {
    Column {
        Surface(elevation = 8.dp) {
            TopAppBar(title = { Text(text = "App Bar") })
        }

        VerticalScroller {
            Column {
                (1..100).forEach { number ->
                    ListItem("Title: $number")
                    if (number != 100) {
                        Divider(color = Color.Black.copy(alpha = 0.12f))
                    }
                }
            }
        }
    }

    println("hello")

    +onActive {
        val job = GlobalScope.launch {
            while (isActive) {
                delay(1000)
                println("alive")
            }
        }

        onDispose {
            println("boom")
            job.cancel()
        }
    }
}

@Composable
private fun ListItem(title: String) {
    Ripple(
            bounded = true,
            children = {
                Clickable(
                        onClick = {
                            println("Clicked $title")
                        },
                        children = {
                            Container(height = 48.dp, expanded = true) {
                                Align(
                                        alignment = Alignment.CenterLeft,
                                        children = {
                                            Padding(left = 16.dp, right = 16.dp) {
                                                Text(text = title)
                                            }
                                        }
                                )
                            }
                        }
                )
            }
    )
}