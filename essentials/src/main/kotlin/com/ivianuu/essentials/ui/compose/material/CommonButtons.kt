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
import androidx.ui.graphics.Image
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.Navigator

@Composable
fun DrawerButton(
    image: Image = +drawableResource(R.drawable.es_ic_menu)
) = composable("DrawerButton") {
    val scaffold = +ambient(ScaffoldAmbient)
    IconButton(
        image = image,
        onClick = { scaffold.toggleDrawer() }
    )
}

@Composable
fun BackButton(
    image: Image = +drawableResource(R.drawable.es_ic_arrow_back)
) = composable("BackButton") {
    val navigator = +inject<Navigator>()
    IconButton(
        image = image,
        onClick = { navigator.pop() }
    )
}

@Composable
fun <T> PopupMenuButton(
    alignment: Alignment = Alignment.Center,
    image: Image = +drawableResource(R.drawable.es_ic_more_vert),
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit
) = composable("PopupMenuButton") {
    PopupMenuTrigger(
        alignment = alignment,
        onCancel = onCancel,
        items = items,
        onSelected = onSelected,
        item = item
    ) { showPopup ->
        IconButton(
            image = image,
            onClick = showPopup
        )
    }
}