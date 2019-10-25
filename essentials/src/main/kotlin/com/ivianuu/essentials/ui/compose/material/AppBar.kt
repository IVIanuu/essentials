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
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.graphics.Image
import androidx.ui.material.AppBarIcon
import androidx.ui.material.TopAppBar
import androidx.ui.material.surface.CurrentBackground
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.image.iconColorForBackground
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.Navigator

@Composable
fun EsTopAppBar(title: String) = composable("EsTopAppBar2") {
    EsTopAppBar(title = { Text(title) })
}

@Composable
fun EsTopAppBar(
    title: @Composable() () -> Unit,
    leading: (@Composable() () -> Unit)? = null,
    trailing: (@Composable() () -> Unit)? = null
) = composable("EsTopAppBar") {
    val navigator = +inject<Navigator>()
    val route = +ambient(RouteAmbient)

    val navigationIconComposable: @Composable (() -> Unit)? =
        if (navigator.backStack.indexOf(route) > 0) {
            { EsNavigationIcon() }
        } else {
            null
        }

    TopAppBar(
        title = title,
        navigationIcon = leading ?: navigationIconComposable,
        actionData = listOfNotNull(trailing),
        action = { it() }
    )
}

@Composable
fun EsNavigationIcon(
    icon: Image = +drawableResource(
        R.drawable.abc_ic_ab_back_material, +iconColorForBackground(
            +ambient(
                CurrentBackground
            )
        )
    )
) = composable("EsNavigationIcon") {
    val navigator = +inject<Navigator>()
    AppBarIcon(
        icon = icon,
        onClick = { navigator.pop() }
    )
}

@Composable
fun <T> PopupMenuAppBarIcon(
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit,
    icon: Image = +drawableResource(
        R.drawable.abc_ic_menu_overflow_material, +iconColorForBackground(
            +ambient(
                CurrentBackground
            )
        )
    )
) = composable("MenuAppBarIcon") {
    PopupMenuTrigger(
        alignment = Alignment.TopRight,
        onCancel = onCancel,
        items = items,
        onSelected = onSelected,
        item = item
    ) { showPopup ->
        AppBarIcon(
            icon = icon,
            onClick = showPopup
        )
    }
}