/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.ivianuu.essentials.kotlin.compiler.exhaustive.exhaustive
import com.ivianuu.essentials.kotlin.compiler.experimental.experimental
import com.ivianuu.essentials.kotlin.compiler.optics.optics
import com.ivianuu.essentials.kotlin.compiler.serializationfix.serializationFix
import org.jetbrains.kotlin.utils.addToStdlib.cast

@Suppress("UnstableApiUsage")
class AppInitializer : ApplicationInitializedListener {
  override fun componentsInitialized() {
    val app = ApplicationManager.getApplication()
    app
      .messageBus.connect(app)
      .subscribe(
        ProjectManager.TOPIC,
        object : ProjectManagerListener {
          override fun projectOpened(project: Project) {
            exhaustive(project.cast())
            experimental(project.cast())
            optics(project.cast())
            serializationFix(project.cast())
          }
        }
      )
  }
}
