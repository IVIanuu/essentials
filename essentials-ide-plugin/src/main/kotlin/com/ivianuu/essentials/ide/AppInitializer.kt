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
