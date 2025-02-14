/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastFilter
import coil.compose.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
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
    ) = Ui<AppPickerScreen> {
      EsScaffold(
        topBar = { EsAppBar { Text(screen.title ?: "Pick an app") } }
      ) {
        ResourceBox(
          produceScopedState(Resource.Idle()) {
            repository.installedApps
              .map { it.fastFilter { screen.appPredicate.test(it) } }
              .flowAsResource()
              .collect { value = it }
          }.value
        ) { apps ->
          EsLazyColumn {
            items(apps) { app ->
              EsListItem(
                onClick = scopedAction { navigator.pop(screen, app) },
                headlineContent = { Text(app.appName) },
                leadingContent = {
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
  }
}
