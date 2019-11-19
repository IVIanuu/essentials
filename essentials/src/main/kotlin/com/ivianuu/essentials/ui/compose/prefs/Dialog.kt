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
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.dialog.dialogRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.kprefs.Pref

@Composable
fun <T> DialogPreference(
    pref: Pref<T>,
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    dialog: @Composable() (() -> Unit) -> Unit
) = composableWithKey("DialogPreference:${pref.key}") {
    val navigator = inject<Navigator>()
    Preference(
        pref = pref,
        title = title,
        summary = summary,
        leading = leading,
        onClick = {
            navigator.push(dialogRoute {
                dialog { navigator.pop() }
            })
        },
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}