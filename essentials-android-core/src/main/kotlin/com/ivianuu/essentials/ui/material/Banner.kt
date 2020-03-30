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

package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.layout.SpacingRow

@Composable
fun Banner(
    leading: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
    actions: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.preferredHeight(24.dp))

        Row(crossAxisAlignment = CrossAxisAlignment.Center) {
            Spacer(Modifier.preferredWidth(16.dp))

            if (leading != null) {
                leading()
                Spacer(Modifier.preferredWidth(16.dp))
            }

            ProvideTextStyle(value = MaterialTheme.typography.body2) {
                ProvideEmphasis(emphasis = EmphasisAmbient.current.high, children = content)
            }

            Spacer(Modifier.preferredWidth(16.dp))
        }

        Spacer(Modifier.preferredHeight(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            gravity = ContentGravity.CenterEnd
        ) {
            Providers(ButtonStyleAmbient provides TextButtonStyle()) {
                SpacingRow(spacing = 8.dp, children = actions)
            }
        }

        Spacer(Modifier.preferredHeight(8.dp))
    }
}
