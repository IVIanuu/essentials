/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.android.asState
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.TmpSwitch

@Composable
fun SwitchListItem(
    dataStore: DataStore<Boolean>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var value by dataStore.asState()
    SwitchListItem(
        value = value,
        onValueChange = { value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading
    )
}

@Composable
fun SwitchListItem(
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
            Box(modifier = Modifier.absorbPointer()) {
                TmpSwitch(
                    checked = value,
                    onCheckedChange = onValueChange
                )
            }
        },
        onClick = { onValueChange(!value) }
    )
}