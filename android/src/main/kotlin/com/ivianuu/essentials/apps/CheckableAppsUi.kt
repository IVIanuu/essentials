/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.popup.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

data class CheckableAppsScreen(
  val checkedApps: Flow<Set<String>>,
  val onCheckedAppsChanged: (Set<String>) -> Unit,
  val appPredicate: AppPredicate,
  val appBarTitle: String
)

@Provide class CheckableAppsUi(
  private val screen: CheckableAppsScreen,
  private val repository: AppRepository
) {
  @Composable fun Content() {
    val checkedApps by screen.checkedApps.collectAsState(emptySet())
    val allApps by remember {
      repository.installedApps
        .map { it.filter { screen.appPredicate(it) } }
    }.collectAsResourceState()

    fun updateCheckedApps(transform: Set<String>.() -> Set<String>) {
      val newCheckedApps = checkedApps.transform()
      screen.onCheckedAppsChanged(newCheckedApps)
    }

    ScreenScaffold(
      topBar = {
        AppBar(
          title = { Text(screen.appBarTitle) },
          actions = {
            PopupMenuButton {
              PopupMenuItem(
                onSelected = {
                  updateCheckedApps {
                    this + allApps.get().map { it.packageName }
                  }
                }
              ) { Text(stringResource(R.string.select_all)) }
              PopupMenuItem(
                onSelected = {
                  updateCheckedApps { emptySet() }
                }
              ) { Text(stringResource(R.string.deselect_all)) }
            }
          }
        )
      }
    ) {
      ResourceVerticalListFor(allApps) { app ->
        val isChecked = app.packageName in checkedApps
        ListItem(
          onClick = {
            updateCheckedApps {
              if (isChecked) this + app.packageName
              else this - app.packageName
            }
          },
          title = { Text(app.appName) },
          leading = {
            Image(
              painter = rememberAsyncImagePainter(AppIcon(packageName = app.packageName)),
              modifier = Modifier.size(40.dp),
              contentDescription = null
            )
          },
          trailing = { Switch(checked = isChecked, onCheckedChange = null) }
        )
      }
    }
  }
}
