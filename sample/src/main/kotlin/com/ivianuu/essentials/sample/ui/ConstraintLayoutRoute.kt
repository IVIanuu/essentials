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

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.layout.ConstraintLayout
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold

val constraintLayoutRoute = composeControllerRoute {
    Scaffold(
        topAppBar = { EsTopAppBar("Constraint layout") },
        body = {
            ConstraintLayout {
                id("title") {
                    Text(
                        text = "Hello Constraint Layout",
                        style = (+MaterialTheme.typography()).h6
                    )
                }

                ConstraintSet {
                    constraints("title") {
                        centerHorizontally(PARENT)
                        centerVertically(PARENT)
                    }
                }
            }
        }
    )
}