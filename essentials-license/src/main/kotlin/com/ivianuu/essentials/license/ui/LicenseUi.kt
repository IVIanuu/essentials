/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.license.ui

import androidx.compose.foundation.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.license.R
import com.ivianuu.essentials.license.data.*
import com.ivianuu.essentials.license.domain.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*

object LicenseKey : Key<Unit>

@Provide val licenseUi = ModelKeyUi<LicenseKey, LicenseModel> {
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

@Provide @Composable fun licenseModel(
  licenseProjectRepository: LicenceProjectRepository,
  ctx: KeyUiContext<LicenseKey>
) = LicenseModel(
  projects = produceResource { licenseProjectRepository.getLicenseProjects().get() },
  openProject = action { project ->
    if (project.url != null)
      ctx.navigator.push(UrlKey(project.url))
  }
)
