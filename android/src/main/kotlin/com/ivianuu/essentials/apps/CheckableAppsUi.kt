/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.collectAsResourceState
import com.ivianuu.essentials.resource.get
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.navigation.ScreenScope
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

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
          modifier = Modifier.clickable {
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
