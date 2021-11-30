/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

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
