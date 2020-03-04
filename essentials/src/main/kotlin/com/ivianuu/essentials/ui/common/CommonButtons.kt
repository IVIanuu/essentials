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

package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.foundation.Icon
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Menu
import com.ivianuu.essentials.ui.material.IconButton
import com.ivianuu.essentials.ui.material.ScaffoldAmbient
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.RouteAmbient

@Composable
fun DrawerButton(icon: VectorAsset = Icons.Default.Menu) {
    val scaffold = ScaffoldAmbient.current
    IconButton(
        icon = { Icon(icon) },
        onClick = { scaffold.isDrawerOpen = !scaffold.isDrawerOpen }
    )
}

@Composable
fun BackButton(icon: VectorAsset = Icons.Default.ArrowBack) {
    val navigator = NavigatorAmbient.current
    IconButton(
        icon = { Icon(icon) },
        onClick = { navigator.popTop() }
    )
}

@Composable
fun NavigationButton() {
    val scaffold = ScaffoldAmbient.current
    val navigator = NavigatorAmbient.current
    val route = RouteAmbient.current
    val canGoBack = remember { navigator.backStack.indexOf(route) > 0 }
    when {
        scaffold.hasDrawer -> {
            DrawerButton()
        }
        canGoBack -> {
            BackButton()
        }
    }
}
