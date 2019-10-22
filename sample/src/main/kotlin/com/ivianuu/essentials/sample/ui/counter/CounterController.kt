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

package com.ivianuu.essentials.sample.ui.counter

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.LayoutSize
import androidx.ui.material.Button
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.mvrx.mvRxViewModel
import com.ivianuu.injekt.parametersOf

fun counterRoute(screen: Int) = composeControllerRoute {
    Scaffold(
        appBar = { EsTopAppBar("Counter") },
        content = {
            VerticalScroller {
                Column(
                    crossAxisSize = LayoutSize.Expand,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val viewModel = +mvRxViewModel<CounterViewModel> {
                        parametersOf(screen)
                    }

                    Text(
                        text = "Screen: $screen",
                        style = +themeTextStyle { h3 }
                    )

                    Button(
                        text = "Screen up",
                        onClick = { viewModel.screenUpClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "Screen down",
                        onClick = { viewModel.screenDownClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "List screen",
                        onClick = { viewModel.listScreenClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "App picker",
                        onClick = { viewModel.appPickerClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "Check apps",
                        onClick = { viewModel.checkAppsClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "Do work",
                        onClick = { viewModel.doWorkClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "Nav bar",
                        onClick = { viewModel.navBarClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "Twilight",
                        onClick = { viewModel.twilightClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "Compose",
                        onClick = { viewModel.composeClicked() }
                    )

                    HeightSpacer(8.dp)

                    Button(
                        text = "About",
                        onClick = { viewModel.aboutClicked() }
                    )
                }
            }
        }
    )
}