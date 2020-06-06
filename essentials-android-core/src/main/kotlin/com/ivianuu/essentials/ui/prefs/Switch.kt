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

package com.ivianuu.essentials.ui.prefs

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.material.Switch

@Composable
fun SwitchPreference(
    box: Box<Boolean>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    SwitchPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading
    )
}

@Composable
fun SwitchPreference(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BasePreference(
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        trailing = {
            Box(modifier = Modifier.absorbPointer()) {
                Switch(
                    checked = value,
                    onCheckedChange = { onValueChange(!value); Unit }
                )
            }
        },
        onClick = { onValueChange(!value); Unit }
    )
}
