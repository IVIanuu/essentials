/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.AmbientIndication
import androidx.compose.material.Icon
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.rememberRef
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.push

@Composable
fun PopupMenuButton(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(size = 40.dp)
            .popupClickable(
                items = items,
                onCancel = onCancel,
                indicationFactory = { RippleIndication(bounded = false) }
            )
            .then(modifier),
        alignment = Alignment.Center
    ) {
        Icon(Icons.Default.MoreVert)
    }
}

@Composable
fun Modifier.popupClickable(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    indicationFactory: @Composable () -> Indication = AmbientIndication.current,
) = composed {
    val navigator = NavigatorAmbient.current

    var coordinates by rememberRef<LayoutCoordinates?> { null }

    onGloballyPositioned { coordinates = it }
        .clickable(indication = indicationFactory()) {
            navigator.push(
                PopupRoute(
                    position = coordinates!!.boundsInRoot,
                    onCancel = onCancel
                ) {
                    PopupMenu(items = items)
                }
            )
        }
}
