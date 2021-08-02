/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.google.accompanist.pager.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Provide val tabsHomeItem = HomeItem("Tabs") { TabsKey }

object TabsKey : Key<Nothing>

@OptIn(ExperimentalPagerApi::class)
@Provide
val tabsUi: KeyUi<TabsKey> = {
  val pagerState = rememberPagerState(TabItems.size)
  val scope = rememberCoroutineScope()
  Scaffold(
    topBar = {
      Surface(
        color = MaterialTheme.colors.primary,
        elevation = 8.dp
      ) {
        Column {
          TopAppBar(
            title = { Text("Tabs") },
            elevation = 0.dp
          )
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
      }
    }
  ) {
    HorizontalPager(pagerState) { page ->
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
