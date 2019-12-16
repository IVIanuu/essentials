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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.Size
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import com.ivianuu.essentials.ui.compose.navigation.navigator
import com.ivianuu.essentials.ui.compose.navigation.route

// todo added centerTitle

@Composable
fun EsTopAppBar(title: String) {
    EsTopAppBar(title = { Text(title) })
}

@Composable
fun EsTopAppBar(
    color: Color = MaterialTheme.colors().primary,
    title: (@Composable() () -> Unit)? = null,
    leading: (@Composable() () -> Unit)? = autoTopAppBarLeadingIcon(),
    trailing: (@Composable() () -> Unit)? = null
) {
    TopAppBar(
        color = color,
        title = title ?: {},
        navigationIcon = leading?.let {
            {
                CurrentIconStyleProvider(appBarIconStyle(color)) {
                    leading()
                }
            }
        },
        actionData = listOfNotNull(trailing),
        action = {
            CurrentIconStyleProvider(appBarIconStyle(color)) {
                it()
            }
        }
    )
}

@Composable
private fun autoTopAppBarLeadingIcon(): (@Composable() () -> Unit)? {
    val scaffold = scaffold
    val navigator = navigator
    val route = route
    return when {
        scaffold.hasDrawer -> {
            { DrawerButton() }
        }
        navigator.backStack.indexOf(route) > 0 -> {
            { BackButton() }
        }
        else -> null
    }
}

@Composable
fun appBarIconStyle(color: Color): IconStyle = IconStyle(
    size = Size(AppBarIconSize, AppBarIconSize),
    color = contentColorFor(color)
)

private val AppBarIconSize = 24.dp
