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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.clickable
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredWidth
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.ProvideEmphasis
import androidx.ui.unit.dp

// todo add list dialog

@Composable
fun SimpleDialogListItem(
    onClick: () -> Unit,
    leading: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .preferredHeightIn(minHeight = 48.dp)
            .clickable(onClick = onClick),
        gravity = ContentGravity.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
                if (leading != null) {
                    leading()
                    Spacer(Modifier.preferredWidth(24.dp))
                }

                title()
            }
        }
    }
}
