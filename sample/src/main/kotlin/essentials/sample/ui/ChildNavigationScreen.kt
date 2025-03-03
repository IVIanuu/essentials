/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import injekt.*

class ChildNavigationScreen : Screen<Unit>

object ChildNavGraph

@Provide val childNavigationHomeItem = HomeItem("Child Navigation") { ChildNavigationScreen() }

@Provide fun childNavigationUi(
  navigationComponent: NavigationComponent<ChildNavGraph>
) = Ui<ChildNavigationScreen> {
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
) : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: ChildNavigationItemScreen
    ): @NavGraph<ChildNavGraph> Ui<ChildNavigationItemScreen> = Ui {
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
          Text("${screen.navigationIndex} ${screen.index}")

          Button(
            enabled = screen.index > 0,
            onClick = action { navigator.popTop() }
          ) {
            Text("Previous")
          }

          Button(onClick = action {
            navigator.push(screen.copy(index = screen.index.inc()))
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
  }
}
