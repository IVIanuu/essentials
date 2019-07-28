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

package com.ivianuu.essentials.sample.ui.list

import androidx.compose.ViewComposition
import androidx.compose.state
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.layout.Alignment
import androidx.ui.material.MaterialTheme
import androidx.ui.material.themeColor
import com.github.ajalt.timberkt.d
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.ComposeController
import com.ivianuu.essentials.ui.compose.core.Surface
import com.ivianuu.essentials.ui.compose.group
import com.ivianuu.essentials.ui.compose.material.Button
import com.ivianuu.essentials.ui.compose.material.CheckBox
import com.ivianuu.essentials.ui.compose.material.DarkMaterialColors
import com.ivianuu.essentials.ui.compose.material.FloatingActionButton
import com.ivianuu.essentials.ui.compose.material.RadioButton
import com.ivianuu.essentials.ui.compose.material.Switch
import com.ivianuu.essentials.ui.compose.material.border
import com.ivianuu.essentials.ui.compose.material.rippleColor
import com.ivianuu.essentials.ui.compose.view.FrameLayout
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.LinearLayout
import com.ivianuu.essentials.ui.compose.view.LinearLayoutOrientation
import com.ivianuu.essentials.ui.compose.view.MATCH_PARENT
import com.ivianuu.essentials.ui.compose.view.ProgressBar
import com.ivianuu.essentials.ui.compose.view.SeekBar
import com.ivianuu.essentials.ui.compose.view.TextView
import com.ivianuu.essentials.ui.compose.view.WRAP_CONTENT
import com.ivianuu.essentials.ui.compose.view.WidthSpacer
import com.ivianuu.essentials.ui.compose.view.backgroundColor
import com.ivianuu.essentials.ui.compose.view.elevation
import com.ivianuu.essentials.ui.compose.view.gravity
import com.ivianuu.essentials.ui.compose.view.height
import com.ivianuu.essentials.ui.compose.view.image
import com.ivianuu.essentials.ui.compose.view.margin
import com.ivianuu.essentials.ui.compose.view.matchParent
import com.ivianuu.essentials.ui.compose.view.onValueChange
import com.ivianuu.essentials.ui.compose.view.orientation
import com.ivianuu.essentials.ui.compose.view.text
import com.ivianuu.essentials.ui.compose.view.textColor
import com.ivianuu.essentials.ui.compose.view.textGravity
import com.ivianuu.essentials.ui.compose.view.value
import com.ivianuu.essentials.ui.compose.view.verticalMargin
import com.ivianuu.essentials.ui.compose.view.width
import com.ivianuu.essentials.ui.compose.view.wrapContent
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.essentials.util.darken
import com.ivianuu.injekt.Inject

val listRoute = controllerRoute<ListController>(options = controllerRouteOptions().fade())

@Inject
class ListController : ComposeController() {

    override fun ViewComposition.build() {
        MaterialTheme(colors = DarkMaterialColors()) {
            with(requireActivity().window) {
                statusBarColor = (+themeColor { primary }).toArgb().darken()
            }

            FrameLayout {
                matchParent()

                LinearLayout {
                    matchParent()
                    orientation(LinearLayoutOrientation.Vertical)
                    gravity(Alignment.TopCenter)
                    backgroundColor(+themeColor { surface })

                    Surface {
                        AppBar()
                        Content()
                    }
                }

                Fab()
            }
        }
    }

    private fun ViewComposition.AppBar() {
        AppBar(
            leading = {
                AppBarIcon(
                    image = Image(res = R.drawable.abc_ic_ab_back_material),
                    onClick = { d { "on nav click" } }
                )
            },
            title = {
                TextView {
                    width(Dp.MATCH_PARENT)
                    height(Dp.WRAP_CONTENT)
                    text("Compose Sample")
                    textGravity(Alignment.Center)
                }
            },
            trailing = {
                LinearLayout {
                    wrapContent()
                    orientation(LinearLayoutOrientation.Horizontal)

                    group {
                        AppBarIcon(
                            image = Image(res = R.drawable.es_ic_link),
                            onClick = { d { "on link click" } }
                        )
                    }
                    WidthSpacer(8.dp)
                    AppBarIcon(
                        image = Image(res = R.drawable.es_ic_torch_on),
                        onClick = { d { "on torch click" } }
                    )
                }
            }
        )
    }

    private fun ViewComposition.Content() {
        SelectionControls()
        Progress()
        Buttons()
    }

    private fun ViewComposition.Progress() {
        ProgressBar { }

        val (seekBarValue, setSeekBarValue) = +state { 50 }

        SeekBar {
            verticalMargin(8.dp)
            value(seekBarValue)
            onValueChange { setSeekBarValue(it) }
        }
    }

    private fun ViewComposition.SelectionControls() {
        LinearLayout {
            width(Dp.MATCH_PARENT)
            height(Dp.WRAP_CONTENT)
            orientation(LinearLayoutOrientation.Horizontal)
            gravity(Alignment.Center)

            val (checked, setChecked) = +state { true }
            CheckBox(checked = checked, onCheckedChange = { setChecked(it) })

            val (selected, setSelected) = +state { true }
            RadioButton(selected = selected, onSelect = { setSelected(!selected) })

            val (checked2, setChecked2) = +state { true }
            Switch(checked = checked2, onCheckedChange = { setChecked2(it) })
        }
    }

    private fun ViewComposition.Buttons() {
        Button {
            text("Default")
        }

        Button {
            border(
                (+themeColor { onSurface }).copy(alpha = 0.12f),
                1.dp
            )
            textColor(+themeColor { primary })
            rippleColor(+themeColor { primary.copy(alpha = 0.24f) })
            elevation(0.dp)
            update { node.stateListAnimator = null }
            backgroundColor(Color.Transparent)
            text("Outlined")
        }

        Button {
            textColor(+themeColor { primary })
            rippleColor(+themeColor { primary.copy(alpha = 0.24f) })
            elevation(0.dp)
            update { node.stateListAnimator = null }
            backgroundColor(Color.Transparent)
            text("Text button")
        }
    }

    private fun ViewComposition.Fab() {
        FloatingActionButton {
            wrapContent()
            image(res = R.drawable.icon_back_white)
            gravity(Alignment.BottomRight)
            margin(16.dp)
        }
    }
}