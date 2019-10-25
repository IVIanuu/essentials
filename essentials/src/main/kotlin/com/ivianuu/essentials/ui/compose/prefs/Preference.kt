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
import androidx.ui.core.Opacity
import androidx.ui.material.ListItem
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun <T> Preference(
    pref: Pref<T>,
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    trailing: @Composable() (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("Preference:${pref.key}") {
    val finalEnabled = enabled && dependencies?.checkAll() ?: true

    Opacity(if (finalEnabled) 1f else 0.5f) {
        ListItem(
            text = title,
            secondaryText = summary,
            icon = leading,
            trailing = trailing,
            singleLineSecondaryText = singleLineSummary,
            onClick = if (finalEnabled) onClick else null
        )
    }
}