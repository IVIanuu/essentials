/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.ivianuu.essentials.apps.coil.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

data class AppPickerKey(
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
) : Key<AppInfo>

@Provide val appPickerUi: ModelKeyUi<AppPickerKey, AppPickerModel> = {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(model.title ?: stringResource(R.string.es_title_app_picker))
        }
      )
    }
  ) {
    ResourceLazyColumnFor(model.filteredApps) { app ->
      ListItem(
        title = { Text(app.appName) },
        leading = {
          Image(
            painter = rememberImagePainter(AppIcon(packageName = app.packageName)),
            modifier = Modifier.size(40.dp),
            contentDescription = null
          )
        },
        onClick = { model.pickApp(app) }
      )
    }
  }
}

@Optics data class AppPickerModel(
  private val allApps: Resource<List<AppInfo>> = Idle,
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
  val pickApp: (AppInfo) -> Unit = {}
) {
  val filteredApps = allApps
    .map { it.filter(appPredicate) }
}

@Provide fun appPickerModel(
  key: AppPickerKey,
  getInstalledApps: GetInstalledAppsUseCase,
  navigator: Navigator,
  scope: InjektCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<AppPickerModel> = scope.state(
  AppPickerModel(
    appPredicate = key.appPredicate,
    title = key.title
  )
) {
  resourceFlow { emit(getInstalledApps()) }
    .update { copy(allApps = it) }
  action(AppPickerModel.pickApp()) { navigator.pop(key, it) }
}
