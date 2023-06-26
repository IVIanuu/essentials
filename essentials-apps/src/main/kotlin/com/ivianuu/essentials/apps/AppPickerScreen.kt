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
import com.ivianuu.essentials.Resource
import com.ivianuu.essentials.collectAsResourceState
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.map
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Model
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

@Provide val appPickerUi = Ui<AppPickerScreen, AppPickerModel> { model ->
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(model.title ?: stringResource(R.string.es_title_app_picker))
        }
      )
    }
  ) {
    ResourceVerticalListFor(model.filteredApps) { app ->
      ListItem(
        modifier = Modifier.clickable { model.pickApp(app) },
        title = { Text(app.appName) },
        leading = {
          Image(
            painter = rememberAsyncImagePainter(AppIcon(packageName = app.packageName)),
            modifier = Modifier.size(40.dp)
          )
        }
      )
    }
  }
}

data class AppPickerModel(
  private val allApps: Resource<List<AppInfo>>,
  val appPredicate: AppPredicate,
  val title: String?,
  val pickApp: (AppInfo) -> Unit
) {
  val filteredApps = allApps
    .map { it.filter { appPredicate(it) } }
}

@Provide fun appPickerModel(
  navigator: Navigator,
  repository: AppRepository,
  screen: AppPickerScreen
) = Model {
  AppPickerModel(
    appPredicate = screen.appPredicate,
    title = screen.title,
    allApps = repository.installedApps.collectAsResourceState().value,
    pickApp = action { app -> navigator.pop(screen, app) }
  )
}
