/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gradle

import com.google.auto.service.*
import com.ivianuu.injekt.gradle.*
import org.gradle.api.*
import org.gradle.api.provider.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.*

@AutoService(KotlinCompilerPluginSupportPlugin::class)
open class EssentialsPlugin : KotlinCompilerPluginSupportPlugin {
  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
    kotlinCompilation.target.project.plugins.hasPlugin(EssentialsPlugin::class.java)

  override fun apply(target: Project) {
    target.plugins.apply(InjektPlugin::class.java)
    target.plugins.apply("com.ivianuu.injekt")
    target.plugins.apply("kotlinx-atomicfu")
    target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
    target.plugins.apply("com.ivianuu.essentials.compose")
    target.plugins.apply("com.google.devtools.ksp")
    target.dependencies.add(
      "ksp",
      "com.ivianuu.essentials:ksp:${BuildConfig.VERSION}"
    )
    target.extensions.add("essentials", EssentialsExtension(target))
  }

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    kotlinCompilation.kotlinOptions.run {
      freeCompilerArgs = freeCompilerArgs + listOf(
        "-XXLanguage:+NewInference",
        "-Xskip-prerelease-check",
        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
      )
      if (kotlinCompilation is KotlinJvmCompilation ||
        kotlinCompilation is KotlinJvmAndroidCompilation)
          (kotlinCompilation.kotlinOptions as KotlinJvmOptions).jvmTarget = "1.8"
    }
    return kotlinCompilation.target.project.provider { emptyList() }
  }

  override fun getCompilerPluginId(): String = "com.ivianuu.essentials"

  override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
    groupId = "com.ivianuu.essentials",
    artifactId = "compiler",
    version = BuildConfig.VERSION
  )
}
