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
import androidx.ui.foundation.Clickable
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.material.ripple

// todo add list dialog

@Composable
fun SimpleDialogListItem(
    onClick: () -> Unit,
    leading: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit
) {
    Clickable(onClick = onClick, modifier = ripple()) {
        Row(
            modifier = LayoutWidth.Fill + LayoutHeight.Min(48.dp) +
                    LayoutPadding(start = 24.dp, end = 24.dp),
            mainAxisAlignment = MainAxisAlignment.Start,
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            ProvideEmphasis(emphasis = MaterialTheme.emphasisLevels().high) {
                if (leading != null) {
                    leading()
                    Spacer(LayoutWidth(24.dp))
                }

                title()
            }
        }
    }
}
