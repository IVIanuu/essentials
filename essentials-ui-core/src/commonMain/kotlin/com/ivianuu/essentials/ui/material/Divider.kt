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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable fun HorizontalDivider(
  modifier: Modifier = Modifier,
  color: Color = LocalContentColor.current.copy(alpha = 0.12f),
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(color)
      .height(1.dp)
      .then(modifier)
  )
}

@Composable fun VerticalDivider(
  modifier: Modifier = Modifier,
  color: Color = LocalContentColor.current.copy(alpha = 0.12f),
) {
  Box(
    modifier = Modifier
      .fillMaxHeight()
      .background(color)
      .width(1.dp)
      .then(modifier)
  )
}
