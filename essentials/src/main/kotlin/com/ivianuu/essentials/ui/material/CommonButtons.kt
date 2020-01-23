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

package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.graphics.Image
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.core.current
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupStyle
import com.ivianuu.essentials.ui.popup.PopupStyleAmbient
import com.ivianuu.essentials.ui.popup.PopupTrigger
import com.ivianuu.essentials.ui.resources.drawableResource

@Composable
fun DrawerButton(
    image: Image = drawableResource(R.drawable.es_ic_menu)
) {
    val scaffold = ScaffoldAmbient.current
    IconButton(
        image = image,
        onClick = { scaffold.isDrawerOpen = !scaffold.isDrawerOpen }
    )
}

@Composable
fun BackButton(
    image: Image = drawableResource(R.drawable.es_ic_arrow_back)
) {
    val navigator = NavigatorAmbient.current
    IconButton(
        image = image,
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

@Composable
fun PopupMenuButton(
    alignment: Alignment = Alignment.TopLeft,
    image: Image = drawableResource(R.drawable.es_ic_more_vert),
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    onCancel: (() -> Unit)? = null,
    items: List<PopupMenu.Item>
) {
    PopupTrigger(
        alignment = alignment,
        onCancel = onCancel,
        popup = {
            PopupMenu(
                items = items,
                style = popupStyle
            )
        },
        child = { showPopup ->
            IconButton(
                image = image,
                onClick = showPopup
            )
        }
    )
}

@Composable
fun <T> PopupMenuButton(
    alignment: Alignment = Alignment.TopLeft,
    image: Image = drawableResource(R.drawable.es_ic_more_vert),
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable (T) -> Unit
) {
    PopupTrigger(
        alignment = alignment,
        onCancel = onCancel,
        popup = {
            PopupMenu(
                items = items,
                onSelected = onSelected,
                item = item,
                style = popupStyle
            )
        },
        child = { showPopup ->
            IconButton(
                image = image,
                onClick = showPopup
            )
        }
    )
}
