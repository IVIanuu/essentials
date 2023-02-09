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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.animation.transition.HorizontalStackTransition
import com.ivianuu.essentials.ui.animation.transition.LocalStackTransition
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.NavigatorContent
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

object ChildNavigationKey : Key<Unit>

@Provide val childNavigationHomeItem = HomeItem("Child Navigation") { ChildNavigationKey }

@Provide fun childNavigationUi() = SimpleKeyUi<ChildNavigationKey> {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text("Child Navigation") })
    }
  ) {
    Column {
      CompositionLocalProvider(
        LocalStackTransition provides HorizontalStackTransition()
      ) {
        (1..3).forEach { navigationIndex ->
          key(navigationIndex) {
            val scope = rememberCoroutineScope()
            val navigator = remember {
              Navigator(
                initialBackStack = listOf(ChildNavigationItemKey(navigationIndex, 0)),
                scope = scope
              )
            }

            NavigatorContent(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
              navigator = navigator
            )
          }
        }
      }
    }
  }
}

data class ChildNavigationItemKey(
  val navigationIndex: Int,
  val index: Int
) : Key<Unit>

@Provide fun childNavigationItemUi(
  ctx: KeyUiContext<ChildNavigationItemKey>
) = SimpleKeyUi<ChildNavigationItemKey> {
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
      Text("${ctx.key.navigationIndex} ${ctx.key.index}")

      Button(
        enabled = ctx.key.index > 0,
        onClick = action { ctx.navigator.popTop() }
      ) {
        Text("Previous")
      }

      Button(onClick = action {
        ctx.navigator.push(ctx.key.copy(index = ctx.key.index.inc()))
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
