/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val tabsHomeItem = HomeItem("Tabs") { TabsScreen() }

class TabsScreen : Screen<Unit>

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Provide val tabsUi = Ui<TabsScreen, Unit> {
  val pagerState = rememberPagerState()
  Scaffold(
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 8.dp
      ) {
        TopAppBar(
          title = { Text("Tabs") },
          bottomContent = {
            TabRow(
              selectedTabIndex = pagerState.currentPage,
              containerColor = MaterialTheme.colorScheme.primary,
              indicator = { tabPositions ->
                /*TabRowDefaults.Indicator(
                  Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )*/
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
      }
    }
  ) {
    HorizontalPager(pageCount = TabItems.size, state = pagerState) { page ->
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
