package com.ivianuu.essentials.ide

import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener

@Suppress("UnstableApiUsage")
class AppInitializer : ApplicationInitializedListener {
    override fun componentsInitialized() {
        val app = ApplicationManager.getApplication()
        println("hello world")
        app
            .messageBus.connect(app)
            .subscribe(
                ProjectManager.TOPIC,
                object : ProjectManagerListener {
                    override fun projectOpened(project: Project) {
                    }
                }
            )
    }
}
