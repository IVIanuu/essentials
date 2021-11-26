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

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.transition.ContainerTransformStackTransition
import com.ivianuu.essentials.ui.animation.transition.ContainerTransformSurface
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scope

data class ContainerTransformDetailsKey(val closedKey: Any) : Key<Unit>

@Provide fun containerTransformDetailsUi(
  scope: Scope<KeyUiScope>
) = KeyUi<ContainerTransformDetailsKey> {
  var listInfo by scope { mutableStateOf(0 to 0) }
  ContainerTransformSurface(key = "opened", elevation = 8.dp, isOpened = false) {
    Scaffold(topBar = { TopAppBar(title = { Text("Details") }) }) {
      val listState = rememberLazyListState(listInfo.first, listInfo.second)
      listInfo = listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
      VerticalList(state = listState) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth()
              .height(250.dp)
              .background(Color.Black.copy(alpha = 0.38f)),
            contentAlignment = Alignment.Center
          ) {
            Image(
              modifier = Modifier.padding(70.dp),
              painterResId = R.drawable.placeholder_image
            )
          }
        }
        item {
          Text(
            modifier = Modifier.padding(
              start = 16.dp,
              end = 16.dp,
              top = 16.dp
            ),
            text = "Title",
            style = MaterialTheme.typography.h4
          )
        }
        item {
          CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
              modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp
              ),
              text = Strings.LoremIpsumChain,
              style = MaterialTheme.typography.body2
            )
          }
        }
      }
    }
  }
}

@Provide
val containerTransformDetailsOptionsFactory = KeyUiOptionsFactory<ContainerTransformDetailsKey> {
  KeyUiOptions(ContainerTransformStackTransition(it.closedKey, "opened"))
}
