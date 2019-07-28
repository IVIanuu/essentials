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

import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import androidx.compose.ViewComposition
import androidx.compose.ambient
import androidx.compose.state
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.layout.Alignment
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.CurrentRippleTheme
import androidx.ui.material.themeColor
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.ComposeController
import com.ivianuu.essentials.ui.compose.core.Surface
import com.ivianuu.essentials.ui.compose.group
import com.ivianuu.essentials.ui.compose.material.Button
import com.ivianuu.essentials.ui.compose.material.CheckBox
import com.ivianuu.essentials.ui.compose.material.RadioButton
import com.ivianuu.essentials.ui.compose.material.Switch
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.LinearLayout
import com.ivianuu.essentials.ui.compose.view.MATCH_PARENT
import com.ivianuu.essentials.ui.compose.view.TextView
import com.ivianuu.essentials.ui.compose.view.WRAP_CONTENT
import com.ivianuu.essentials.ui.compose.view.WidthSpacer
import com.ivianuu.essentials.ui.compose.view.backgroundColor
import com.ivianuu.essentials.ui.compose.view.gravity
import com.ivianuu.essentials.ui.compose.view.height
import com.ivianuu.essentials.ui.compose.view.matchParent
import com.ivianuu.essentials.ui.compose.view.onClick
import com.ivianuu.essentials.ui.compose.view.orientation
import com.ivianuu.essentials.ui.compose.view.text
import com.ivianuu.essentials.ui.compose.view.textGravity
import com.ivianuu.essentials.ui.compose.view.width
import com.ivianuu.essentials.ui.compose.view.wrapContent
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.injekt.Inject

val listRoute = controllerRoute<ListController>(options = controllerRouteOptions().fade())

@Inject
class ListController : ComposeController() {

    override fun ViewComposition.build() {
        MaterialTheme {

            LinearLayout {
                matchParent()
                orientation(VERTICAL)
                gravity(Alignment.TopCenter)
                backgroundColor(+themeColor { surface })

                Surface {
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
                                orientation(HORIZONTAL)

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

                    LinearLayout {
                        width(Dp.MATCH_PARENT)
                        height(Dp.WRAP_CONTENT)
                        orientation(HORIZONTAL)
                        gravity(Alignment.Center)

                        val oldRippleTheme = +ambient(CurrentRippleTheme)

                        CurrentRippleTheme.Provider(
                            oldRippleTheme.copy(
                                colorCallback = { Color.Magenta.copy(alpha = 0.24f) }
                            )
                        ) {
                            val (checked, setChecked) = +state { true }
                            CheckBox(checked = checked, onCheckedChange = { setChecked(it) })

                            val (selected, setSelected) = +state { true }
                            RadioButton(selected = selected, onSelect = { setSelected(!selected) })

                            val (checked2, setChecked2) = +state { true }
                            Switch(checked = checked2, onCheckedChange = { setChecked2(it) })
                        }
                    }


                    (1..10).forEach { i ->
                        Button(sourceLocation() + i) {
                            width(100.dp)
                            text("Hello")
                            onClick { d { "on click" } }
                        }
                    }
                }
            }
        }
    }
}