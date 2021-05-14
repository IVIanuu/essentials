package com.ivianuu.essentials.ide

import com.intellij.ide.*
import com.intellij.openapi.application.*
import com.intellij.openapi.project.*
import com.ivianuu.essentials.kotlin.compiler.optics.*
import org.jetbrains.kotlin.resolve.extensions.*

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
            SyntheticResolveExtension.registerExtension(
              project,
              OpticsResolveExtension()
            )
          }
        }
      )
  }
}
