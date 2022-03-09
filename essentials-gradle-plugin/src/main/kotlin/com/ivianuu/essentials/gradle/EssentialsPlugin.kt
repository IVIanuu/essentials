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
    target.extensions.add("essentials", EssentialsExtension(target))
  }

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    kotlinCompilation.kotlinOptions.run {
      freeCompilerArgs = freeCompilerArgs + listOf(
        "-Xuse-ir",
        "-Xskip-runtime-version-check",
        "-XXLanguage:+NewInference",
        "-Xskip-prerelease-check"
      )
      if (kotlinCompilation is KotlinJvmCompilation ||
        kotlinCompilation is KotlinJvmAndroidCompilation
      ) {
        (kotlinCompilation.kotlinOptions as KotlinJvmOptions).jvmTarget = "1.8"
        freeCompilerArgs += listOf(
          "-Xallow-jvm-ir-dependencies",
          "-Xjvm-default=enable",
        )
      }
    }
    return kotlinCompilation.target.project.provider { emptyList() }
  }

  override fun getCompilerPluginId(): String = "com.ivianuu.essentials"

  override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
    groupId = "com.ivianuu.essentials",
    artifactId = "essentials-compiler-plugin",
    version = BuildConfig.VERSION
  )
}
