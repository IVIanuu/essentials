/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gradle

import com.google.auto.service.AutoService
import com.ivianuu.injekt.gradle.InjektPlugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

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
        "-Xskip-runtime-version-check",
        "-XXLanguage:+NewInference",
        "-Xskip-prerelease-check",
        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
      )
      if (kotlinCompilation is KotlinJvmCompilation ||
        kotlinCompilation is KotlinJvmAndroidCompilation
      ) {
        (kotlinCompilation.kotlinOptions as KotlinJvmOptions).jvmTarget = "1.8"
        freeCompilerArgs += listOf("-Xallow-jvm-ir-dependencies")
      }
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
