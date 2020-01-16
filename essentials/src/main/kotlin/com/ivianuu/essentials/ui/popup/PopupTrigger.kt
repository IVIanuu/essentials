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

package com.ivianuu.essentials.ui.popup

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.IntPxPosition
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnPositioned
import androidx.ui.core.PxPosition
import androidx.ui.core.round
import androidx.ui.layout.Wrap
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.composehelpers.current
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

// todo better name?
@Composable
fun PopupTrigger(
    alignment: Alignment = Alignment.BottomLeft,
    onCancel: (() -> Unit)? = null,
    popup: @Composable() () -> Unit,
    child: @Composable() (showPopup: () -> Unit) -> Unit
) {
    val navigator = NavigatorAmbient.current

    Wrap {
        val coordinatesHolder =
            holder<LayoutCoordinates?> { null }
        OnPositioned { coordinatesHolder.value = it }

        val showPopup = {
            val coordinates = coordinatesHolder.value!!

            val width = coordinates.size.width.round()
            val height = coordinates.size.height.round()
            val globalPosition = coordinates.localToRoot(PxPosition.Origin)
            val left = globalPosition.x.round()
            val top = globalPosition.y.round()
            val right = left + width
            val bottom = top + height
            val centerX = left + width / 2
            val centerY = top + height / 2

            val position = when (alignment) {
                Alignment.TopLeft -> IntPxPosition(left, top)
                Alignment.TopCenter -> IntPxPosition(centerX, top)
                Alignment.TopRight -> IntPxPosition(right, top)
                Alignment.CenterLeft -> IntPxPosition(left, centerY)
                Alignment.Center -> IntPxPosition(centerX, centerY)
                Alignment.CenterRight -> IntPxPosition(right, centerY)
                Alignment.BottomLeft -> IntPxPosition(left, bottom)
                Alignment.BottomCenter -> IntPxPosition(centerX, bottom)
                Alignment.BottomRight -> IntPxPosition(right, bottom)
            }

            navigator.push(
                PopupRoute(
                    position = position,
                    onCancel = onCancel,
                    popup = popup
                )
            )
        }

        child(showPopup)
    }
}
