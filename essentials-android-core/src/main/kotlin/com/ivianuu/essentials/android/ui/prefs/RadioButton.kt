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

package com.ivianuu.essentials.android.ui.prefs

import androidx.compose.Composable
import androidx.compose.Pivotal
import com.ivianuu.essentials.android.ui.common.AbsorbPointer
import com.ivianuu.essentials.android.ui.material.RadioButton
import com.ivianuu.essentials.store.Box

@Composable
fun RadioButtonPreference(
    @Pivotal box: Box<Boolean>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null
) {
    RadioButtonPreference(
        valueController = ValueController(box),
        enabled = enabled,
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading
    )
}

@Composable
fun RadioButtonPreference(
    valueController: ValueController<Boolean>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null
) {
    PreferenceWrapper(
        valueController = valueController,
        enabled = enabled,
        dependencies = dependencies
    ) { context ->
        PreferenceLayout(
            title = title,
            summary = summary,
            leading = leading,
            trailing = {
                AbsorbPointer {
                    RadioButton(
                        selected = context.currentValue,
                        onSelect = if (context.shouldBeEnabled) {
                            {}
                        } else null
                    )
                }
            },
            enabled = context.shouldBeEnabled,
            onClick = { context.setIfOk(!context.currentValue); Unit }
        )
    }
}
