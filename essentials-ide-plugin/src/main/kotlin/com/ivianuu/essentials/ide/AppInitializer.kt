/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.ide.*
import com.intellij.openapi.application.*
import com.intellij.openapi.project.*
import com.ivianuu.essentials.kotlin.compiler.exhaustive.*
import com.ivianuu.essentials.kotlin.compiler.experimental.*
import com.ivianuu.essentials.kotlin.compiler.optics.optics
import com.ivianuu.essentials.kotlin.compiler.serializationfix.*
import org.jetbrains.kotlin.utils.addToStdlib.*

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
