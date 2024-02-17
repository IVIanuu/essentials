/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.pager.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide val tabsHomeItem = HomeItem("Tabs") { TabsScreen() }

class TabsScreen : Screen<Unit> {
  @Provide companion object {
    private val TabItems = listOf(Color.Blue, Color.Red, Color.Magenta, Color.Green, Color.Cyan)

    @Provide val ui = Ui<TabsScreen, Unit> {
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
  }
}
