/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.resource.collectAsResourceState
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.map

class AppPickerScreen(
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
) : Screen<AppInfo>

@Provide fun appPickerUi(
  navigator: Navigator,
  repository: AppRepository,
  screen: AppPickerScreen
) = Ui<AppPickerScreen, Unit> {
  ScreenScaffold(
    topBar = { AppBar { Text(screen.title ?: stringResource(R.string.title_app_picker)) } }
  ) {
    ResourceVerticalListFor(
      remember {
        repository.installedApps
          .map { it.filter { screen.appPredicate(it) } }
      }
        .collectAsResourceState().value
    ) { app ->
      ListItem(
        modifier = Modifier.clickable(onClick = action { navigator.pop(screen, app) }),
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
