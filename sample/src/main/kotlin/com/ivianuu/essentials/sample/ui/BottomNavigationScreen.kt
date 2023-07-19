/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.AnimatedContent
import com.ivianuu.essentials.ui.material.NavigationBar
import com.ivianuu.essentials.ui.material.NavigationBarItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val bottomNavigationHomeItem = HomeItem("Bottom navigation") { BottomNavigationScreen() }

class BottomNavigationScreen : Screen<Unit>

@Provide val bottomNavigationUi = Ui<BottomNavigationScreen, Unit> {
  var selectedItem by remember { mutableStateOf(BottomNavItem.entries.first()) }

  Scaffold(
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
            icon = { Icon(item.icon) },
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
    icon = R.drawable.es_ic_settings,
    color = Color.Green
  )
}
