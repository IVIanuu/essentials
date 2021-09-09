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

package com.ivianuu.essentials.license.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.license.R
import com.ivianuu.essentials.license.data.Project
import com.ivianuu.essentials.license.domain.GetLicenseProjectsUseCase
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowResultAsResource
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

object LicenseKey : Key<Unit>

@Provide val licenseUi: ModelKeyUi<LicenseKey, LicenseModel> = {
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_licenses_title) }) }) {
    ResourceLazyColumnFor(model.projects) { project ->
      Project(
        project = project,
        onClick = { model.openProject(project) }
      )
    }
  }
}

@Composable private fun Project(onClick: () -> Unit, project: Project) {
  ListItem(
    title = { Text(project.project) },
    onClick = onClick
  )
}

@Optics data class LicenseModel(
  val projects: Resource<List<Project>> = Idle,
  val openProject: (Project) -> Unit = {}
)

@Provide fun licenseModel(
  getProjects: GetLicenseProjectsUseCase,
  navigator: Navigator,
  scope: InjektCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<LicenseModel> = scope.state(LicenseModel()) {
  flow { emit(getProjects()) }
    .flowResultAsResource()
    .update { copy(projects = it) }

  action(LicenseModel.openProject()) { project ->
    if (project.url != null)
      navigator.push(UrlKey(project.url))
  }
}
