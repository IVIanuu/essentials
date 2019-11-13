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

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.common.scrolling.Scroller
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.PrimaryMaterialColors
import com.ivianuu.essentials.ui.compose.layout.FlutterWrap
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.colorForCurrentBackground

val chipsRoute = composeControllerRoute {
    Scaffold(
        topAppBar = { EsTopAppBar("Chips") },
        body = {
            Scroller {
                FlutterWrap(spacing = 8.dp, runSpacing = 8.dp) {
                    Names.forEach {
                        Chip(it)
                    }
                }
            }
        }
    )
}

@Composable
private fun Chip(name: String) = composable("Chip:$name") {
    val color = +memo { PrimaryMaterialColors.toList().shuffled().first() }
    Surface(color = color, shape = RoundedCornerShape(16.dp)) {
        Container(
            height = 32.dp,
            padding = EdgeInsets(
                left = 12.dp,
                right = 12.dp
            )
        ) {
            Text(
                text = name,
                style = (+themeTextStyle { body2 }).copy(
                    color = +colorForCurrentBackground()
                )
            )
        }
    }
}

private val Names = listOf(
    "Hans Dieter Josef",
    "Alex Meier",
    "Claude Hardy",
    "Wilhelm Richards",
    "Gustova Da Costa",
    "Lisa Wray",
    "Michael Clark",
    "Yusuf Grimes",
    "Mustafa Müller"
)