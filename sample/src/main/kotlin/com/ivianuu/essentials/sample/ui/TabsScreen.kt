/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val tabsHomeItem = HomeItem("Tabs") { TabsScreen() }

class TabsScreen : Screen<Unit>

@Provide val tabsUi = Ui<TabsScreen, Unit> {
  val pagerState = rememberPagerState { TabItems.size }
  ScreenScaffold(
    topBar = {
      AppBar(
        title = { Text("Tabs") },
        bottomContent = {
          TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colors.primary,
            indicator = { tabPositions ->
              TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
              )
            }
          ) {
            TabItems.indices.forEach { page ->
              Tab(
                selected = pagerState.currentPage == page,
                onClick = action { pagerState.animateScrollToPage(page) },
                text = { Text("Item: $page") }
              )
            }
          }
        }
      )
    },
    maxTopBarSize = 56.dp + 48.dp + LocalInsets.current.top
  ) {
    HorizontalPager(state = pagerState) { page ->
      val color = TabItems[page]
      Surface(color = color) {
        Text(
          text = "Index: $page",
          modifier = Modifier.center()
        )
      }
    }
  }
}

private val TabItems = listOf(Color.Blue, Color.Red, Color.Magenta, Color.Green, Color.Cyan)
