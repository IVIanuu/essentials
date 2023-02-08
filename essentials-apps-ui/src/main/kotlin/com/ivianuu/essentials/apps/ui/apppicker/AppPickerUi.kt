/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.ui.apppicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.AppPredicate
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bindResource
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide

data class AppPickerKey(
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
) : Key<AppInfo>

@Provide val appPickerUi = ModelKeyUi<AppPickerKey, AppPickerModel> {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(title ?: stringResource(R.string.es_title_app_picker))
        }
      )
    }
  ) {
    ResourceVerticalListFor(filteredApps) { app ->
      ListItem(
        modifier = Modifier.clickable { pickApp(app) },
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
  ctx: KeyUiContext<AppPickerKey>,
  repository: AppRepository
) = Model {
  AppPickerModel(
    appPredicate = ctx.key.appPredicate,
    title = ctx.key.title,
    allApps = repository.installedApps.bindResource(),
    pickApp = action { app -> ctx.navigator.pop(ctx.key, app) }
  )
}
