/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun Subheader(
  modifier: Modifier = Modifier,
  text: @Composable () -> Unit
) {
  Box(
    modifier = Modifier.height(48.dp)
      .fillMaxWidth()
      .padding(
        start = 16.dp,
        top = 16.dp,
        end = 16.dp
      )
      .then(modifier),
    contentAlignment = Alignment.CenterStart
  ) {
    CompositionLocalProvider(
      LocalTextStyle provides MaterialTheme.typography.body2,
      LocalContentColor provides MaterialTheme.colors.secondary,
      content = text
    )
  }
}
