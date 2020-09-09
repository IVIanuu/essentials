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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputFilter
import androidx.compose.ui.input.pointer.PointerInputModifier
import androidx.compose.ui.unit.IntSize

@Composable
fun Modifier.absorbPointer(
    absorb: Boolean = true,
): Modifier = composed {
    remember { AbsorbPointerGestureFilter() }
        .also { it.absorb = absorb }
}

private class AbsorbPointerGestureFilter : PointerInputFilter(), PointerInputModifier {

    var absorb = true

    override val pointerInputFilter: PointerInputFilter
        get() = this

    private val consumedIds = mutableSetOf<PointerId>()

    override fun onPointerInput(
        changes: List<PointerInputChange>,
        pass: PointerEventPass,
        bounds: IntSize
    ): List<PointerInputChange> {
        // todo
        return changes
        /*return if (absorb && (pass == PointerEventPass.Initial ||
                    pass == PointerEventPass.PreDown ||
                    pass == PointerEventPass.PostDown)
        ) {
            changes.map { change ->
                if (change.changedToDown()) {
                    change.consumeDownChange().also { consumedChange ->
                        consumedIds += consumedChange.id
                    }
                } else {
                    change
                }
            }
        } else {
            changes.map { change ->
                if (change.id in consumedIds) {
                    change.copy(consumed = change.consumed.copy(downChange = false))
                        .also { consumedIds -= change.id }
                } else {
                    change
                }
            }
        }*/
    }

    override fun onCancel() {
        consumedIds.clear()
    }
}
