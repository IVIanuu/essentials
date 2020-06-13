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
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Menu
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.material.ScaffoldAmbient
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

@Composable
fun DrawerButton(icon: @Composable () -> Unit = { Icon(Icons.Default.Menu) }) {
    val scaffold = ScaffoldAmbient.current
    IconButton(
        onClick = { scaffold.isDrawerOpen = !scaffold.isDrawerOpen },
        icon = icon
    )
}

@Composable
fun BackButton(icon: @Composable () -> Unit = { Icon(Icons.Default.ArrowBack) }) {
    val onBackPressedDispatcher = onBackPressedDispatcherOwner
        .onBackPressedDispatcher
    IconButton(
        onClick = { onBackPressedDispatcher.onBackPressed() },
        icon = icon
    )
}

@Composable
fun NavigationButton() {
    val scaffold = ScaffoldAmbient.current
    val navigator = NavigatorAmbient.current
    val canGoBack = remember {
        navigator.backStack.size > 1 ||
                (navigator.popsLastRoute && navigator.backStack.isNotEmpty())
    }
    when {
        scaffold.hasDrawer -> DrawerButton()
        canGoBack -> BackButton()
    }
}
