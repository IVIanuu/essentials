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
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Box

@Composable
fun SimplePreference(
    onClick: () -> Unit,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Dependencies(dependencies = dependencies ?: emptyList()) { dependenciesOk ->
        Box(modifier = drawOpacity(opacity = if (dependenciesOk) 1f else 0.5f)) {
            PreferenceLayout(
                title = title,
                summary = summary,
                leading = leading,
                trailing = trailing,
                enabled = dependenciesOk,
                onClick = onClick
            )
        }
    }
}
