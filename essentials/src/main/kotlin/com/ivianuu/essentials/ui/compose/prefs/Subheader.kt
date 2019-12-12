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
import androidx.compose.Pivotal
import androidx.ui.core.Opacity
import com.ivianuu.essentials.ui.compose.material.Subheader

@Composable
fun PreferenceSubheader(
    @Pivotal text: String,
    dependencies: List<Dependency<*>>? = null
) {
    Dependencies(dependencies ?: emptyList()) { dependenciesOk ->
        Opacity(opacity = if (dependenciesOk) 1f else 0.5f) {
            Subheader(text)
        }
    }
}

@Composable
fun PreferenceSubheader(
    dependencies: List<Dependency<*>>? = null,
    text: @Composable() () -> Unit
) {
    Dependencies(dependencies ?: emptyList()) { dependenciesOk ->
        Opacity(opacity = if (dependenciesOk) 1f else 0.5f) {
            Subheader(text)
        }
    }
}
