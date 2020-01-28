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
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.ArrowBack
import com.ivianuu.essentials.material.icons.filled.Menu
import com.ivianuu.essentials.material.icons.filled.MoreVert
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.essentials.ui.painter.Renderable
import com.ivianuu.essentials.ui.painter.VectorRenderable
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupStyle
import com.ivianuu.essentials.ui.popup.PopupStyleAmbient
import com.ivianuu.essentials.ui.popup.PopupTrigger

@Composable
fun DrawerButton(image: Renderable = VectorRenderable(Icons.Default.Menu)) {
    val scaffold = ScaffoldAmbient.current
    IconButton(
        image = image,
        onClick = { scaffold.isDrawerOpen = !scaffold.isDrawerOpen }
    )
}

@Composable
fun BackButton(image: Renderable = VectorRenderable(Icons.Default.ArrowBack)) {
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
    image: Renderable = VectorRenderable(Icons.Default.MoreVert),
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
    image: Renderable = VectorRenderable(Icons.Default.MoreVert),
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    selectedItem: T,
    onSelected: (T) -> Unit,
    item: @Composable (T, Boolean) -> Unit
) {
    PopupTrigger(
        alignment = alignment,
        onCancel = onCancel,
        popup = {
            PopupMenu(
                items = items,
                selectedItem = selectedItem,
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
