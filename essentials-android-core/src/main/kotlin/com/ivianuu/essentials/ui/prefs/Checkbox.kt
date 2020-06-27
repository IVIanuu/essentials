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
import androidx.ui.material.Checkbox
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.ui.datastore.asState
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.material.ListItem

@Composable
fun CheckboxListItem(
    dataStore: DataStore<Boolean>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    CheckboxListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading
    )
}

@Composable
fun CheckboxListItem(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        trailing = {
            Checkbox(
                checked = value,
                onCheckedChange = { onValueChange(!value); Unit },
                modifier = Modifier.absorbPointer()
            )
        },
        onClick = { onValueChange(!value); Unit }
    )
}
