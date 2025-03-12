/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.overlay.*
import injekt.*

class TestSharedElementScreen : OverlayScreen<Unit>

@Provide @Composable fun TestSharedElementUi(): Ui<TestSharedElementScreen> {
  Box(
    contentAlignment = Alignment.Center,
    modifier = with(LocalScreenAnimationScope.current) {
      Modifier.fillMaxSize()
        .background(Color.Black.copy(alpha =0.3f))
        .animateEnterExit()
    }
  ) {
    Surface(
      onClick = {},
      modifier = with(LocalScreenAnimationScope.current) {
        Modifier
          .size(300.dp)
          .sharedElement(
            rememberSharedContentState("key"),
            this
          )
      },
      color = MaterialTheme.colorScheme.primaryContainer
    ) {

    }
  }
}

@Provide val dialogsHomeItem = HomeItem("Dialogs") { DialogsScreen() }

class DialogsScreen : Screen<Unit>

@Provide @Composable fun DialogsUi(navigator: Navigator): Ui<DialogsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Dialogs") } }) {
    EsLazyColumn(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      item {
        val items = listOf(1, 2, 3, 4, 5)
        var selected by remember { mutableIntStateOf(1) }
        Button(
          onClick = scopedAction {
            navigator.push(
              SingleChoiceListScreen(
                items = items,
                selected = selected
              ) { it.toString() }
            )
              ?.let { selected = it }
          }
        ) {
          Text("Single choice")
        }
      }
      item {
        val items = listOf("A", "B", "C")
        var selected by remember { mutableStateOf(items.toSet()) }
        Button(
          onClick = action {
            navigator.push(
              MultiChoiceListScreen(
                items = items,
                selected = selected
              )
            )
              ?.let { selected = it }
          }
        ) { Text("Multi choice") }
      }
      item {
        val primaryColor = MaterialTheme.colorScheme.primary
        var currentColor by remember { mutableStateOf(primaryColor) }
        Button(
          onClick = action {
            navigator.push(
              ColorPickerScreen(initialColor = currentColor)
            )?.let { currentColor = it }
          }
        ) { Text("Color choice") }
      }
      item {
        var current by remember { mutableStateOf("") }
        Button(
          onClick = action {
            navigator.push(
              TextInputScreen(
                label = "text...",
                initial = current
              )
            )?.let { current = it }
          }
        ) { Text("Text") }
      }
    }
  }
}
