/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.ui.apppicker

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*

data class AppPickerKey(
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
) : Key<AppInfo>

@Provide val appPickerUi = ModelKeyUi<AppPickerKey, AppPickerModel> {
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
            painter = rememberImagePainter(AppIcon(packageName = app.packageName)),
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
    .map { it.filter(appPredicate) }
}

@Provide fun appPickerModel(
  appRepository: AppRepository,
  ctx: KeyUiContext<AppPickerKey>
) = Model {
  AppPickerModel(
    appPredicate = ctx.key.appPredicate,
    title = ctx.key.title,
    allApps = appRepository.installedApps.bindResource(),
    pickApp = action { app -> ctx.navigator.pop(ctx.key, app) }
  )
}
