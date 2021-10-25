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
    target.extensions.add("essentials", EssentialsExtension(target))
  }

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    kotlinCompilation.kotlinOptions.run {
      freeCompilerArgs = freeCompilerArgs + listOf(
        "-Xuse-ir",
        "-Xskip-runtime-version-check",
        "-XXLanguage:+NewInference",
        "-Xuse-experimental=kotlin.Experimental",
        "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
        "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
        "-Xuse-experimental=kotlin.experimental.ExperimentalTypeInference",
        "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
        "-Xuse-experimental=kotlin.time.ExperimentalTime",
        "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
        "-Xuse-experimental=kotlinx.coroutines.InternalCoroutinesApi",
        "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
        "-Xuse-experimental=kotlinx.serialization.UnstableDefault",
        "-Xuse-experimental=kotlin.time.ExperimentalTime",
        "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer",
        "-Xuse-experimental=androidx.compose.ExperimentalComposeApi",
        "-Xuse-experimental=org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI",
        "-Xuse-experimental=androidx.compose.runtime.ExperimentalComposeApi",
        "-Xuse-experimental=androidx.compose.ui.focus.ExperimentalFocus",
        "-Xuse-experimental=androidx.compose.foundation.ExperimentalFoundationApi",
        "-Xuse-experimental=androidx.compose.material.ExperimentalMaterialApi",
        "-Xuse-experimental=androidx.compose.runtime.InternalComposeApi",
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
