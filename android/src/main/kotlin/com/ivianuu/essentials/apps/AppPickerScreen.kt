/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

class AppPickerScreen(
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
) : Screen<AppInfo> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      repository: AppRepository,
      screen: AppPickerScreen
    ) = Ui<AppPickerScreen, Unit> {
      ScreenScaffold(
        topBar = { AppBar { Text(screen.title ?: stringResource(R.string.title_app_picker)) } }
      ) {
        ResourceVerticalListFor(
          produceResourceState {
            repository.installedApps
              .map { it.filter { screen.appPredicate(it) } }
              .collect { emit(it) }
          }.value
        ) { app ->
          ListItem(
            onClick = action { navigator.pop(screen, app) },
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
  }
}
