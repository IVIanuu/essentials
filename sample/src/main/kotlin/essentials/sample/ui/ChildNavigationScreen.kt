/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import essentials.compose.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

class ChildNavigationScreen : Screen<Unit>

object ChildNavGraph

@Provide val childNavigationHomeItem = HomeItem("Child Navigation") { ChildNavigationScreen() }

@Provide @Composable fun ChildNavigationUi(
  navigationComponent: NavigationComponent<ChildNavGraph>
): Ui<ChildNavigationScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Child Navigation") } }) {
    Column {
      (1..3).forEach { navigationIndex ->
        key(navigationIndex) {
          val scope = rememberCoroutineScope()
          val navigator = remember {
            Navigator(scope, listOf(ChildNavigationItemScreen(navigationIndex, 0)))
          }

          NavigatorContent(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            navigator = navigator,
            navigationComponent = navigationComponent
          )
        }
      }
    }
  }
}

data class ChildNavigationItemScreen(
  val navigationIndex: Int,
  val index: Int
) : Screen<Unit>

@Provide @Composable fun ChildNavigationItemUi(
  context: ScreenContext<ChildNavigationItemScreen> = inject
): @NavGraph<ChildNavGraph> Ui<ChildNavigationItemScreen> {
  val color = Colors.shuffled().first()

  Surface(
    color = color,
    contentColor = guessingContentColorFor(color)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("${context.screen.navigationIndex} ${context.screen.index}")

      Button(
        enabled = context.screen.index > 0,
        onClick = action { navigator().popTop() }
      ) {
        Text("Previous")
      }

      Button(onClick = action {
        navigator().push(context.screen.copy(index = context.screen.index.inc()))
      }) {
        Text("Next")
      }
    }
  }
}

private val Colors = listOf(
  Color.Red,
  Color.Green,
  Color.Blue,
  Color.Magenta,
  Color.Cyan
)
