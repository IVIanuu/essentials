package com.ivianuu.essentials.ide

import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.ivianuu.essentials.kotlin.compiler.optics.OpticsResolveExtension
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

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
