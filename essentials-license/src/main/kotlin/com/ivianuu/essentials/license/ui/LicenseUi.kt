/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.license.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bindResource
import com.ivianuu.essentials.license.R
import com.ivianuu.essentials.license.data.Project
import com.ivianuu.essentials.license.domain.LicenceProjectRepository
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide

class LicenseKey : Key<Unit>

@Provide val licenseUi = Ui<LicenseKey, LicenseModel> { model ->
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_licenses_title) }) }) {
    ResourceVerticalListFor(model.projects) { project ->
      ListItem(
        modifier = Modifier.clickable { model.openProject(project) },
        title = { Text(project.project) }
      )
    }
  }
}

data class LicenseModel(
  val projects: Resource<List<Project>>,
  val openProject: (Project) -> Unit
)

@Provide fun licenseModel(
  navigator: Navigator,
  repository: LicenceProjectRepository
) = Model {
  LicenseModel(
    projects = repository.licenseProjects.bindResource(),
    openProject = action { project ->
      if (project.url != null)
        navigator.push(UrlKey(project.url))
    }
  )
}
