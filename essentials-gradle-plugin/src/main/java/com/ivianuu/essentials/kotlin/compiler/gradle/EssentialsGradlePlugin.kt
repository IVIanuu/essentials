package com.ivianuu.essentials.kotlin.compiler.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class EssentialsGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            "essentials-kotlin-compiler",
            EssentialsGradlePlugin::class.java
        )
    }
}