/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide val bottomNavigationHomeItem = HomeItem("Bottom navigation") { BottomNavigationScreen() }

class BottomNavigationScreen : Screen<Unit> {
  @Provide companion object {
    @Provide val ui = Ui<BottomNavigationScreen> {
      var selectedItem by remember { mutableStateOf(BottomNavItem.entries.first()) }

      ScreenScaffold(
        topBar = { AppBar { Text("Bottom navigation") } },
        bottomBar = {
          NavigationBar(
            backgroundColor = MaterialTheme.colors.primary,
            elevation = 0.dp
          ) {
            BottomNavItem.entries.forEach { item ->
              NavigationBarItem(
                alwaysShowLabel = false,
                selected = item == selectedItem,
                onClick = { selectedItem = item },
                icon = { Icon(item.icon, null) },
                label = { Text(item.title) }
              )
            }
          }
        },
        floatingActionButton = {
          FloatingActionButton(onClick = {}) {
            Icon(Icons.Default.Done, null)
          }
        }
      ) {
        AnimatedContent(selectedItem) { item ->
          VerticalList(
            modifier = Modifier.fillMaxSize()
              .background(item.color)
          ) {
            (1..100).forEach { item ->
              println("compose item $item")
              item {
                ListItem(title = { Text("Item $item") })
              }
            }
          }
        }
      }
    }

    private enum class BottomNavItem(
      val title: String,
      val icon: ImageVector,
      val color: Color
    ) {
      HOME(
        title = "Home",
        icon = Icons.Default.Home,
        color = Color.Yellow
      ),
      MAILS(
        title = "Mails",
        icon = Icons.Default.Email,
        color = Color.Red
      ),
      SEARCH(
        title = "Search",
        icon = Icons.Default.Search,
        color = Color.Blue
      ),
      SCHEDULE(
        title = "Schedule",
        icon = Icons.Default.ViewAgenda,
        color = Color.Cyan
      ),
      SETTINGS(
        title = "Settings",
        icon = Icons.Default.Settings,
        color = Color.Green
      )
    }
  }
}
