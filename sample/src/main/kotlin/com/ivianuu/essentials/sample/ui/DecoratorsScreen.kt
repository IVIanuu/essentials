/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide val decoratorsHomeItem = HomeItem("Decorators") { DecoratorsScreen() }

class DecoratorsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide val ui = Ui<DecoratorsScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("Decorators") } }) {
        VerticalList {
          (1..10).forEach { itemIndex ->
            item {
              ListItem(title = { Text("Item $itemIndex") })
            }
          }
        }
      }
    }
  }
}

@Provide val sampleListDecorator = ListDecorator {
  item {
    val screen = catch { LocalScope.current }.getOrNull()?.screen
    if (screen is DecoratorsScreen)
      Text("Sample decorator before content $screen")
  }

  content()

  item {
    val screen = catch { LocalScope.current }.getOrNull()?.screen
    if (screen is DecoratorsScreen)
      Text("Sample decorator after content $screen")
  }
}

@Provide val sampleKeyUiDecorator = ScreenDecorator decorator@ { content ->
  val screen = LocalScope.current.screen
  if (screen !is DecoratorsScreen) {
    content()
    return@decorator
  } else {
    Column {
      Box(
        modifier = Modifier
          .weight(1f)
          .consumeWindowInsets(
            PaddingValues(
              bottom = with(LocalDensity.current) {
                WindowInsets.navigationBars.getBottom(this).toDp()
              }
            )
          )
      ) { content() }

      Surface(color = MaterialTheme.colors.primary, elevation = 8.dp) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "This is a bottom decorator",
            style = MaterialTheme.typography.h3
          )
        }
      }
    }
  }
}
