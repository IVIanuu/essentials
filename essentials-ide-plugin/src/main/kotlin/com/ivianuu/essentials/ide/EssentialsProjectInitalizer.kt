/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class EssentialsProjectInitializer : ProjectManagerListener {
  override fun projectOpened(project: Project) {
  }
}
