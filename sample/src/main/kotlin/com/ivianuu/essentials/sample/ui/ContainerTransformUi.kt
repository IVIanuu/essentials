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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ComponentStorage
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.scoped
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.animation.transition.ContainerTransformSurface
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide val containerTransformHomeItem = HomeItem("Container transform") { ContainerTransformKey }

object ContainerTransformKey : Key<Unit>

@Provide fun containerTransformUi(
  navigator: Navigator,
  storage: ComponentStorage<UiComponent>
): KeyUi<ContainerTransformKey> = {
  var listInfo by storage.scoped("list_state") { mutableStateOf(0 to 0) }
  ContainerTransformSurface(key = "opened", elevation = 8.dp, isOpened = true) {
    Scaffold(
      topBar = { TopAppBar(title = { Text("Container transform") }) },
      floatingActionButton = {
        ContainerTransformSurface(
          key = "fab",
          isOpened = false,
          color = MaterialTheme.colors.secondary,
          cornerSize = 28.dp,
          elevation = 6.dp
        ) {
          val scope = rememberCoroutineScope()
          Box(
            modifier = Modifier
              .size(56.dp)
              .clickable {
                scope.launch {
                  navigator.push(ContainerTransformDetailsKey("fab"))
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add)
          }
        }
      }
    ) {
      val listState = rememberLazyListState(listInfo.first, listInfo.second)
      listInfo = listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
      VerticalList(state = listState) {
        item { BigDetailsCard(navigator) }
        item { SmallDetailsCard(navigator) }
        item {
          Row {
            DetailsGridCard(Modifier.weight(0.5f), "grid item 0_0", navigator)
            DetailsGridCard(Modifier.weight(0.5f), "grid item 0_1", navigator)
          }
        }
        item {
          Row {
            DetailsGridCard(Modifier.weight(0.33f), "grid item 1_0", navigator)
            DetailsGridCard(Modifier.weight(0.33f), "grid item 1_1", navigator)
            DetailsGridCard(Modifier.weight(0.33f), "grid item 1_2", navigator)
          }
        }
        items(10) { DetailsListItem(it, navigator) }
      }
    }
  }
}

@Composable private fun BigDetailsCard(navigator: Navigator) {
  ContainerTransformSurface(
    key = "big card",
    isOpened = false,
    modifier = Modifier.padding(8.dp),
    cornerSize = 4.dp,
    elevation = 2.dp
  ) {
    val scope = rememberCoroutineScope()
    Column(
      modifier = Modifier.height(300.dp)
        .fillMaxWidth()
        .clickable {
          scope.launch {
            navigator.push(ContainerTransformDetailsKey("big card"))
          }
        },
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .background(Color.Black.copy(alpha = 0.38f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          modifier = Modifier.size(100.dp),
          painterResId = R.drawable.placeholder_image
        )
      }
      ListItem(
        title = { Text(Strings.Title) },
        subtitle = { Text(Strings.Text) }
      )
      CompositionLocalProvider(
        LocalContentAlpha provides ContentAlpha.medium
      ) {
        Text(
          modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
          ),
          text = Strings.LoremIpsum,
          maxLines = 2,
          style = MaterialTheme.typography.body2
        )
      }
    }
  }
}

@Composable private fun SmallDetailsCard(navigator: Navigator) {
  ContainerTransformSurface(
    key = "small card",
    isOpened = false,
    modifier = Modifier.padding(8.dp),
    cornerSize = 4.dp,
    elevation = 2.dp
  ) {
    val scope = rememberCoroutineScope()
    Row(
      modifier = Modifier.height(125.dp)
        .fillMaxWidth()
        .clickable {
          scope.launch {
            navigator.push(ContainerTransformDetailsKey("small card"))
          }
        },
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(125.dp)
          .background(Color.Black.copy(alpha = 0.38f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          modifier = Modifier.padding(16.dp),
          painterResId = R.drawable.placeholder_image
        )
      }
      Column(modifier = Modifier.padding(16.dp)) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
          Text(
            text = Strings.Title,
            style = MaterialTheme.typography.subtitle1
          )
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          Text(
            text = Strings.LoremIpsum,
            maxLines = 2,
            style = MaterialTheme.typography.body2
          )
        }
      }
    }
  }
}

@Composable private fun DetailsGridCard(
  modifier: Modifier = Modifier,
  key: Any,
  navigator: Navigator
) {
  ContainerTransformSurface(
    key = key,
    isOpened = false,
    modifier = modifier
      .padding(8.dp),
    cornerSize = 4.dp,
    elevation = 2.dp
  ) {
    val scope = rememberCoroutineScope()
    Column(
      modifier = Modifier.clickable {
        scope.launch {
          navigator.push(ContainerTransformDetailsKey(key))
        }
      }
    ) {
      Box(
        modifier = Modifier
          .height(100.dp)
          .fillMaxWidth()
          .background(Color.Black.copy(alpha = 0.38f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          modifier = Modifier.padding(16.dp),
          painterResId = R.drawable.placeholder_image
        )
      }

      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center
      ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
          Text(
            text = Strings.Title,
            style = MaterialTheme.typography.subtitle1
          )
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          Text(
            text = Strings.LoremIpsum,
            maxLines = 2,
            style = MaterialTheme.typography.body2
          )
        }
      }
    }
  }
}

@Composable private fun DetailsListItem(
  index: Int,
  navigator: Navigator
) {
  ContainerTransformSurface(
    key = "list item $index",
    isOpened = false
  ) {
    val scope = rememberCoroutineScope()
    ListItem(
      leading = {
        Image(
          modifier = Modifier.size(40.dp),
          painterResId = R.drawable.avatar_logo
        )
      },
      title = { Text("List item $index") },
      subtitle = { Text(Strings.Text) },
      onClick = {
        scope.launch {
          navigator.push(ContainerTransformDetailsKey("list item $index"))
        }
      }
    )
  }
}
