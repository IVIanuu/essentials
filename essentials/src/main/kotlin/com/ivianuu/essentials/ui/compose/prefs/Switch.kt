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

package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Switch
import com.ivianuu.essentials.ui.compose.common.AbsorbPointer
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.kprefs.Pref

@Composable
fun SwitchPreference(
    pref: Pref<Boolean>,
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((Boolean) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composableWithKey("SwitchPreference:${pref.key}") {
    fun valueChanged(newValue: Boolean) {
        if (onChange?.invoke(newValue) != false) {
            pref.set(newValue)
        }
    }

    Preference(
        pref = pref,
        title = title,
        summary = summary,
        leading = leading,
        trailing = {
            AbsorbPointer {
                Switch(
                    color = MaterialTheme.colors()().secondary,
                    checked = pref.get(),
                    onCheckedChange = {}
                )
            }
        },
        onClick = { valueChanged(!pref.get()) },
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}