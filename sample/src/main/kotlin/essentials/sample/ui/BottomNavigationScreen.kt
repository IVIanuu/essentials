/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package essentials.sample.ui

import android.annotation.*
import androidx.compose.animation.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.util.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Provide val bottomNavigationHomeItem = HomeItem("Bottom navigation") { BottomNavigationScreen() }

class BottomNavigationScreen : Screen<Unit>

@Provide @Composable fun BottomNavigationUi(
  context: ScreenContext<BottomNavigationScreen> = inject
): Ui<BottomNavigationScreen> {
  var selectedItem by remember { mutableStateOf(BottomNavItem.entries.first()) }

  EsScaffold(
    topBar = { EsAppBar { Text("Bottom navigation") } },
    bottomBar = {
      EsNavigationBar {
        BottomNavItem.entries.fastForEach { item ->
          NavigationBarItem(
            alwaysShowLabel = true,
            selected = item == selectedItem,
            onClick = { selectedItem = item },
            icon = { Icon(item.icon, null) },
            label = { Text(item.title) }
          )
        }
      }
    }
  ) {
    @SuppressLint("UnusedContentLambdaTargetStateParameter")
    AnimatedContent(selectedItem) { item ->
      EsLazyColumn {
        (1..100).forEach { item ->
          item {
            SectionListItem(
              sectionType = sectionTypeOf(item - 1, 100),
              title = { Text("Item $item") }
            )
          }
        }
      }
    }
  }
}

private enum class BottomNavItem(
  val title: String,
  val icon: ImageVector
) {
  HOME(
    title = "Home",
    icon = Icons.Default.Home
  ),
  MAILS(
    title = "Mails",
    icon = Icons.Default.Email
  ),
  SEARCH(
    title = "Search",
    icon = Icons.Default.Search
  ),
  SCHEDULE(
    title = "Schedule",
    icon = Icons.Default.ViewAgenda
  ),
  SETTINGS(
    title = "Settings",
    icon = Icons.Default.Settings
  )
}
