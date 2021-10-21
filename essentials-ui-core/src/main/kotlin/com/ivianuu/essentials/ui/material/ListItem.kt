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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp

@Composable fun ListItem(
  modifier: Modifier = Modifier,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  trailing: @Composable (() -> Unit)? = null,
  contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
  textPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
  val minHeight = if (subtitle != null) {
    if (leading == null) 72.dp else 80.dp
  } else {
    if (leading == null) 56.dp else 64.dp
  }

  Box(
    modifier = modifier
      .defaultMinSize(minHeight = minHeight)
      .fillMaxWidth(),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // leading
      if (leading != null) {
        Box(
          modifier = Modifier
            .heightIn(minHeight),
          contentAlignment = Alignment.CenterStart
        ) {
          Box(
            modifier = Modifier
              .padding(
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding()
              ),
            contentAlignment = Alignment.Center
          ) {
            CompositionLocalProvider(
              LocalContentAlpha provides ContentAlpha.high,
              content = leading
            )
          }
        }
      }

      // content
      Box(
        modifier = Modifier
          .weight(1f)
          .padding(textPadding),
        contentAlignment = Alignment.CenterStart
      ) {
        Column(
          modifier = Modifier
            .padding(
              top = contentPadding.calculateTopPadding(),
              bottom = contentPadding.calculateBottomPadding()
            ),
          verticalArrangement = Arrangement.Center
        ) {
          if (title != null) {
            ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
              CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.high,
                content = title
              )
            }
          }
          if (subtitle != null) {
            ProvideTextStyle(value = MaterialTheme.typography.body2) {
              CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium,
                content = subtitle
              )
            }
          }
        }
      }

      // trailing
      if (trailing != null) {
        Box(
          modifier = Modifier
            .height(minHeight),
          contentAlignment = Alignment.CenterEnd
        ) {
          Box(
            modifier = Modifier.padding(
              top = contentPadding.calculateTopPadding(),
              end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
              bottom = contentPadding.calculateBottomPadding()
            ),
            contentAlignment = Alignment.Center
          ) {
            CompositionLocalProvider(
              LocalContentAlpha provides ContentAlpha.high,
              content = trailing
            )
          }
        }
      }
    }
  }
}
