/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Provide val tabsHomeItem = HomeItem("Tabs") { TabsKey }

object TabsKey : Key<Unit>

@OptIn(ExperimentalPagerApi::class)
@Provide val tabsUi = KeyUi<TabsKey> {
  val pagerState = rememberPagerState()
  val scope = rememberCoroutineScope()
  Scaffold(
    topBar = {
      Surface(
        color = MaterialTheme.colors.primary,
        elevation = 8.dp
      ) {
        TopAppBar(
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
                  onClick = {
                    scope.launch {
                      pagerState.animateScrollToPage(page)
                    }
                  },
                  text = { Text("Item: $page") }
                )
              }
            }
          }
        )
      }
    }
  ) {
    HorizontalPager(count = TabItems.size, state = pagerState) { page ->
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
