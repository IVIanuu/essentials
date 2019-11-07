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
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.material.TopAppBar
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator

@Composable
fun EsTopAppBar(title: String) = composable("EsTopAppBar2") {
    EsTopAppBar(title = { Text(title) })
}

@Composable
fun EsTopAppBar(
    title: @Composable() () -> Unit,
    leading: (@Composable() () -> Unit)? = +autoTopAppBarLeadingIcon(),
    trailing: (@Composable() () -> Unit)? = null
) = composable("EsTopAppBar") {
    TopAppBar(
        title = title,
        navigationIcon = leading?.let {
            {
                CurrentIconStyleAmbient.Provider(+appBarIconStyle()) {
                    it()
                }
            }
        },
        actionData = listOfNotNull(trailing),
        action = {
            CurrentIconStyleAmbient.Provider(+appBarIconStyle()) {
                it()
            }
        }
    )
}

private fun autoTopAppBarLeadingIcon() = effectOf<(@Composable() () -> Unit)?> {
    val scaffold = +ambient(ScaffoldAmbient)
    val navigator = +inject<Navigator>()
    val route = +ambient(RouteAmbient)
    when {
        scaffold.hasDrawer -> {
            { DrawerButton() }
        }
        navigator.backStack.indexOf(route) > 0 -> {
            { BackButton() }
        }
        else -> null
    }
}

fun appBarIconStyle() = effectOf<IconStyle> {
    IconStyle(color = +colorForCurrentBackground())
}