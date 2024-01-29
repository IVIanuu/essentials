/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.collectAsResourceState
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide

class AppPickerScreen(
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
) : Screen<AppInfo>

@Provide val appPickerUi = Ui<AppPickerScreen, AppPickerState> { state ->
  Scaffold(
    topBar = {
      AppBar {
        Text(state.title ?: stringResource(R.string.es_title_app_picker))
      }
    }
  ) {
    ResourceVerticalListFor(state.filteredApps) { app ->
      ListItem(
        modifier = Modifier.clickable { state.pickApp(app) },
        title = { Text(app.appName) },
        leading = {
          Image(
            painter = rememberAsyncImagePainter(AppIcon(packageName = app.packageName)),
            modifier = Modifier.size(40.dp),
            contentDescription = null
          )
        }
      )
    }
  }
}

data class AppPickerState(
  private val allApps: Resource<List<AppInfo>>,
  val appPredicate: AppPredicate,
  val title: String?,
  val pickApp: (AppInfo) -> Unit
) {
  val filteredApps = allApps
    .map { it.filter { appPredicate(it) } }
}

@Provide fun appPickerPresenter(
  navigator: Navigator,
  repository: AppRepository,
  screen: AppPickerScreen
) = Presenter {
  AppPickerState(
    appPredicate = screen.appPredicate,
    title = screen.title,
    allApps = repository.installedApps.collectAsResourceState().value,
    pickApp = action { app -> navigator.pop(screen, app) }
  )
}
