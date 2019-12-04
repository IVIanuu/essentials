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
import androidx.ui.graphics.Image
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.compose.common.asIconComposable
import com.ivianuu.essentials.ui.compose.common.asTextComposable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.dialog.dialogRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator

@Composable
fun <T> DialogPreference(
    box: Box<T>,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    dialog: @Composable() (PreferenceContext<T>, () -> Unit) -> Unit
) = composableWithKey("DialogPreference:$box") {
    DialogPreference(
        box = box,
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies,
        dialog = dialog,
        title = title.asTextComposable(),
        summary = summary.asTextComposable(),
        leading = image.asIconComposable()
    )
}

@Composable
fun <T> DialogPreference(
    box: Box<T>,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: (@Composable() () -> Unit)? = null,
    summary: (@Composable() () -> Unit)? = null,
    leading: (@Composable() () -> Unit)? = null,
    dialog: @Composable() (PreferenceContext<T>, () -> Unit) -> Unit
) = composableWithKey("DialogPreference:$box") {
    val navigator = inject<Navigator>()
    PreferenceWrapper(
        box = box,
        enabled = enabled,
        dependencies = dependencies,
        onChange = onChange
    ) { context ->
        PreferenceLayout(
            title = title,
            summary = summary,
            leading = leading,
            onClick = if (!context.shouldBeEnabled) null else {
                {
                    navigator.push(dialogRoute {
                        dialog(context) { navigator.pop() }
                    })
                }
            }
        )
    }
}
