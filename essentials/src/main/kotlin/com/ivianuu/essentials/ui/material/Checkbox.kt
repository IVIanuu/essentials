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
import androidx.compose.Immutable
import androidx.ui.core.Modifier
import androidx.ui.foundation.selection.ToggleableState
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.core.ambientOf
import com.ivianuu.essentials.ui.core.current

@Immutable
data class CheckboxStyle(val color: Color)

val CheckboxStyleAmbient = ambientOf<CheckboxStyle?> { null }

@Composable
fun DefaultCheckboxStyle(color: Color = MaterialTheme.colors().secondary) =
    CheckboxStyle(color = color)

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier.None,
    style: CheckboxStyle = CheckboxStyleAmbient.current ?: DefaultCheckboxStyle()
) {
    androidx.ui.material.TriStateCheckbox(
        value = ToggleableState(checked),
        onClick = onCheckedChange?.let { { it(!checked) } },
        color = style.color,
        modifier = modifier
    )
}

@Composable
fun TriStateCheckbox(
    value: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier.None,
    style: CheckboxStyle = CheckboxStyleAmbient.current ?: DefaultCheckboxStyle()
) {
    androidx.ui.material.TriStateCheckbox(
        value = value,
        onClick = onClick,
        color = style.color,
        modifier = modifier
    )
}
