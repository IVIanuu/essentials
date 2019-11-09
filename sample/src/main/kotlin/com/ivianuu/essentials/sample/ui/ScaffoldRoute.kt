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

import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.compose.common.BlockChildTouches
import com.ivianuu.essentials.ui.compose.common.ScrollableList
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.EsCheckbox
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem

val scaffoldRoute = composeControllerRoute {
    val (showFab, setShowFab) = +state { false }
    val (fabPosition, setFabPosition) = +state { Scaffold.FabPosition.End }

    Scaffold(
        topAppBar = { EsTopAppBar("Scaffold") },
        body = {
            ScrollableList {
                SimpleListItem(
                    title = { Text("Show fab") },
                    trailing = {
                        BlockChildTouches {
                            EsCheckbox(checked = showFab, onCheckedChange = {})
                        }
                    },
                    onClick = { setShowFab(!showFab) }
                )
            }
        },
        fabPosition = fabPosition,
        fab = if (showFab) ({
            FloatingActionButton("Click me")
        }) else null
    )
}