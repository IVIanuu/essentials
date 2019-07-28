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
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.layout.Alignment
import androidx.ui.material.MaterialTheme
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.ComposeController
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.Button
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.LinearLayout
import com.ivianuu.essentials.ui.compose.view.MATCH_PARENT
import com.ivianuu.essentials.ui.compose.view.TextView
import com.ivianuu.essentials.ui.compose.view.WRAP_CONTENT
import com.ivianuu.essentials.ui.compose.view.WidthSpacer
import com.ivianuu.essentials.ui.compose.view.gravity
import com.ivianuu.essentials.ui.compose.view.height
import com.ivianuu.essentials.ui.compose.view.matchParent
import com.ivianuu.essentials.ui.compose.view.onClick
import com.ivianuu.essentials.ui.compose.view.orientation
import com.ivianuu.essentials.ui.compose.view.text
import com.ivianuu.essentials.ui.compose.view.textAppearance
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
                            textAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
                            textGravity(Alignment.Center)
                        }
                    },
                    trailing = {
                        LinearLayout {
                            wrapContent()
                            orientation(HORIZONTAL)

                            AppBarIcon(
                                image = Image(res = R.drawable.es_ic_link),
                                onClick = { d { "on link click" } }
                            )
                            WidthSpacer(8.dp)
                            AppBarIcon(
                                image = Image(res = R.drawable.es_ic_torch_on),
                                onClick = { d { "on torch click" } }
                            )
                        }
                    }
                )

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