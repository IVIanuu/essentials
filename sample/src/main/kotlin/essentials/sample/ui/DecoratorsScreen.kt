/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Provide val decoratorsHomeItem = HomeItem("Decorators") { DecoratorsScreen() }

class DecoratorsScreen : Screen<Unit>

@Provide @Composable fun DecoratorsUi(): Ui<DecoratorsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Decorators") } }) {
    EsLazyColumn {
      (1..10).forEach { itemIndex ->
        item {
          EsListItem(headlineContent = { Text("Item $itemIndex") })
        }
      }
    }
  }
}

@Provide val sampleListDecorator = ListDecorator {
  item(null) {
    val screen = catch { LocalScope.current }.getOrNull()?.screen
    if (screen is DecoratorsScreen)
      Text("Sample decorator before content $screen")
  }

  content()

  item(null) {
    val screen = catch { LocalScope.current }.getOrNull()?.screen
    if (screen is DecoratorsScreen)
      Text("Sample decorator after content $screen")
  }
}

@Provide fun SampleUiDecorator(screen: Screen<*>) = ScreenDecorator { content ->
  if (screen !is DecoratorsScreen) {
    content()
    return@ScreenDecorator
  } else {
    Column {
      Box(
        modifier = Modifier
          .weight(1f)
          .consumeWindowInsets(WindowInsets.navigationBars)
      ) { content() }

      Surface(color = MaterialTheme.colorScheme.primary, shadowElevation = 8.dp) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "This is a bottom decorator",
            style = MaterialTheme.typography.displaySmall
          )
        }
      }
    }
  }
}
