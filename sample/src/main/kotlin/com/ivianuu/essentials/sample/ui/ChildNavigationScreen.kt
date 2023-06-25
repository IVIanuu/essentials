/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.animation.slideHorizontally
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.essentials.ui.navigation.NavGraph
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.NavigatorContent
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenContextComponent
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

class ChildNavigationScreen : Screen<Unit>

object ChildNavGraph

@Provide val childNavigationHomeItem = HomeItem("Child Navigation") { ChildNavigationScreen() }

@Provide fun childNavigationUi(
  screenContextComponent: ScreenContextComponent<ChildNavGraph>
) = Ui<ChildNavigationScreen, Unit> {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text("Child Navigation") })
    }
  ) {
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
            screenContextComponent = screenContextComponent,
            defaultTransitionSpec = { slideHorizontally() }
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

@Provide fun childNavigationItemUi(
  navigator: Navigator,
  screen: ChildNavigationItemScreen
): @NavGraph<ChildNavGraph> Ui<ChildNavigationItemScreen, Unit> = Ui {
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
