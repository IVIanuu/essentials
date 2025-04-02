/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gradle

import injekt.gradle.*
import org.gradle.api.*
import org.gradle.api.provider.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*

open class EssentialsPlugin : KotlinCompilerPluginSupportPlugin {
  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
    kotlinCompilation.target.project.plugins.hasPlugin(EssentialsPlugin::class.java)

  override fun apply(target: Project) {
    target.plugins.apply(InjektPlugin::class.java)
    target.plugins.apply("org.jetbrains.kotlin.plugin.compose")
    target.plugins.apply("org.jetbrains.kotlinx.atomicfu")
    target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
    target.plugins.apply("com.google.devtools.ksp")
    target.plugins.apply("io.github.ivianuu.injekt")
    target.dependencies.add(
      "ksp",
      "essentials:ksp:${BuildConfig.VERSION}"
    )
    target.extensions.add("essentials", EssentialsExtension(target))
    target.extensions.getByType(KotlinBaseExtension::class.java)
      .jvmToolchain(11)
  }

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    kotlinCompilation.kotlinOptions.run {
      freeCompilerArgs = freeCompilerArgs + listOf(
        "-XXLanguage:+NewInference",
        "-Xskip-prerelease-check",
        "-opt-in=kotlin.experimental.ExperimentalTypeInference",
        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
        "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
        "-opt-in=androidx.compose.animation.core.InternalAnimationApi",
        "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
        "-Xcontext-parameters"
      )
    }
    return kotlinCompilation.target.project.provider { emptyList() }
  }

  override fun getCompilerPluginId(): String = "essentials"

  override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
    groupId = "essentials",
    artifactId = "compiler",
    version = BuildConfig.VERSION
  )
}
