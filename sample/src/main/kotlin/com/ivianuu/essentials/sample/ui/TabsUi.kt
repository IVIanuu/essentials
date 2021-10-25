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

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide val tabsHomeItem = HomeItem("Tabs") { TabsKey }

object TabsKey : Key<Unit>

@Provide val tabsUi: KeyUi<TabsKey> = {
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
