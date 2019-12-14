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
import com.ivianuu.essentials.ui.compose.dialog.DialogRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator

@Composable
fun <T> DialogPreference(
    valueController: ValueController<T>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    dialog: @Composable() (PreferenceContext<T>, () -> Unit) -> Unit
) {
    val navigator = inject<Navigator>()
    PreferenceWrapper(
        valueController = valueController,
        enabled = enabled,
        dependencies = dependencies
    ) { context ->
        PreferenceLayout(
            title = title,
            summary = summary,
            leading = leading,
            onClick = if (!context.shouldBeEnabled) null else {
                {
                    navigator.push(DialogRoute {
                        dialog(context) { navigator.pop() }
                    })
                }
            }
        )
    }
}
