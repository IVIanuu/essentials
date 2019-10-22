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

import androidx.animation.TweenBuilder
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.lifecycle.viewModelScope
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Alignment
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Padding
import androidx.ui.material.Button
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.about.aboutRoute
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.ui.appPickerRoute
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarController
import com.ivianuu.essentials.sample.work.WorkScheduler
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.secureSettingsRoute
import com.ivianuu.essentials.twilight.twilightSettingsRoute
import com.ivianuu.essentials.ui.changehandler.verticalFade
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.coroutines.launchOnActive
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Popup
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.ScaffoldAmbient
import com.ivianuu.essentials.ui.compose.mvrx.mvRxViewModel
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.copy
import com.ivianuu.essentials.ui.navigation.director.horizontal
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        },
        fabConfiguration = Scaffold.FabConfiguration(
            Scaffold.FabPosition.Center,
            fab = {
                val scaffold = +ambient(ScaffoldAmbient)
                FloatingActionButton(
                    text = "Click me",
                    onClick = {
                        scaffold.showOverlay { dismissOverlay ->
                            Padding(4.dp) {
                                val opacity = +animatedFloat(0f)
                                Opacity(opacity = opacity.value) {
                                    Popup(
                                        alignment = Alignment.TopRight,
                                        content = {
                                            ConstrainedBox(
                                                constraints = DpConstraints(
                                                    minWidth = 200.dp,
                                                    minHeight = 100.dp
                                                )
                                            ) {
                                                Padding(8.dp) {
                                                    Column(
                                                        crossAxisAlignment = CrossAxisAlignment.Center
                                                    ) {
                                                        Text("Item 1")
                                                        HeightSpacer(8.dp)
                                                        Text("Item 2")
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }

                                +launchOnActive {
                                    opacity.animateTo(
                                        targetValue = 1f,
                                        anim = TweenBuilder<Float>().apply { duration = 300 },
                                        onEnd = { reason, value ->

                                        }
                                    )
                                    delay(3000)
                                    opacity.animateTo(
                                        targetValue = 0f,
                                        anim = TweenBuilder<Float>().apply { duration = 300 },
                                        onEnd = { _, _ ->
                                            dismissOverlay()
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        )
    )
}

@Inject
class CounterViewModel(
    @Param screen: Int,
    private val navigator: Navigator,
    private val navBarController: NavBarController,
    private val secureSettingsHelper: SecureSettingsHelper,
    private val toaster: Toaster,
    private val workScheduler: WorkScheduler
) : MvRxViewModel<CounterState>(
    CounterState(
        screen
    )
) {

    private var navBarHidden = false

    fun screenUpClicked() {
        navigator.push(counterRoute(state.screen.inc()))
    }

    fun screenDownClicked() {
        navigator.pop()
    }

    fun listScreenClicked() {
        navigator.push(listRoute)
    }

    fun checkAppsClicked() {
        navigator.push(checkAppsRoute)
    }

    fun appPickerClicked() {
        viewModelScope.launch {
            val app = navigator.push<AppInfo>(appPickerRoute(launchableOnly = true))
            if (app != null) {
                toaster.toast("App picked ${app.appName}")
            }
        }
    }

    fun doWorkClicked() {
        workScheduler.scheduleWork()
    }

    fun twilightClicked() {
        navigator.push(
            twilightSettingsRoute.copy(
                options = controllerRouteOptions().horizontal()
            )
        )
    }

    fun navBarClicked() {
        if (secureSettingsHelper.canWriteSecureSettings()) {
            navBarHidden = !navBarHidden
            navBarController.setNavBarConfig(NavBarConfig(navBarHidden))
        } else {
            navigator.push(secureSettingsRoute(true))
        }
    }

    fun composeClicked() {
        navigator.push(
            composeRoute.copy(
                options = controllerRouteOptions().verticalFade()
            )
        )
    }

    fun aboutClicked() {
        navigator.push(
            aboutRoute(privacyPolicyUrl = "https://www.google.com").copy(
                options = controllerRouteOptions().verticalFade()
            )
        )
    }
}

data class CounterState(val screen: Int)