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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.ui.AppIcon
import com.ivianuu.essentials.apps.ui.AppPredicate
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.resourceFromFlow
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
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

@Provide @Composable fun appPickerModel(
  appRepository: AppRepository,
  ctx: KeyUiContext<AppPickerKey>
) = AppPickerModel(
  appPredicate = ctx.key.appPredicate,
  title = ctx.key.title,
  allApps = resourceFromFlow { appRepository.installedApps },
  pickApp = action { app -> ctx.navigator.pop(ctx.key, app) }
)
