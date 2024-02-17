/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide val bottomNavigationHomeItem = HomeItem("Bottom navigation") { BottomNavigationScreen() }

class BottomNavigationScreen : Screen<Unit> {
  @Provide companion object {
    @Provide val ui = Ui<BottomNavigationScreen, Unit> {
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
                icon = { Icon(painterResource(item.icon), null) },
                label = { Text(item.title) }
              )
            }
          }
        }
      ) {
        AnimatedContent(selectedItem) { item ->
          Box(
            modifier = Modifier.fillMaxSize()
              .background(item.color)
          )
        }
      }
    }

    private enum class BottomNavItem(
      val title: String,
      val icon: Int,
      val color: Color
    ) {
      HOME(
        title = "Home",
        icon = R.drawable.ic_home,
        color = Color.Yellow
      ),
      MAILS(
        title = "Mails",
        icon = R.drawable.ic_email,
        color = Color.Red
      ),
      SEARCH(
        title = "Search",
        icon = R.drawable.ic_search,
        color = Color.Blue
      ),
      SCHEDULE(
        title = "Schedule",
        icon = R.drawable.ic_view_agenda,
        color = Color.Cyan
      ),
      SETTINGS(
        title = "Settings",
        icon = R.drawable.ic_settings,
        color = Color.Green
      )
    }
  }
}
