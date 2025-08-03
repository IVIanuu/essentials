/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

import com.android.build.gradle.*
import com.vanniktech.maven.publish.*
import org.jetbrains.kotlin.compose.compiler.gradle.*
import org.jetbrains.kotlin.gradle.tasks.*

buildscript {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://androidx.dev/storage/compose-compiler/repository")
    maven("https://jitpack.io")
    maven("https://api.xposed.info/")
  }

  dependencies {
    classpath(Deps.androidGradlePlugin)
    classpath(Deps.dokkaGradlePlugin)
    classpath(Deps.essentialsGradlePlugin)
    classpath(Deps.Kotlin.gradlePlugin)
    classpath(Deps.mavenPublishGradlePlugin)
  }
}

allprojects {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
    maven("https://api.xposed.info/")
    maven("https://androidx.dev/storage/compose-compiler/repository")
  }

  plugins.withId("com.vanniktech.maven.publish") {
    extensions.getByType<MavenPublishBaseExtension>().run {
      publishToMavenCentral(SonatypeHost.S01)
    }
  }

  configurations.configureEach {
    resolutionStrategy.dependencySubstitution {
      substitute(module("io.github.ivianuu.essentials:ksp"))
        .using(project(":ksp"))
      substitute(module("io.github.ivianuu.essentials:compiler"))
        .using(project(":compiler"))
    }
  }

  fun setupAndroid() {
    extensions.getByType<BaseExtension>().run {
      compileSdkVersion(Build.compileSdk)
      defaultConfig {
        minSdk = Build.minSdk
        targetSdk = Build.targetSdk
      }
      namespace = "essentials.${name.replace("-", ".")}"
    }
    afterEvaluate {
      extensions.getByType<ComposeCompilerGradlePluginExtension>()?.run {
        reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
        metricsDestination.set(layout.buildDirectory.dir("compose_compiler"))
        stabilityConfigurationFile.set(
          rootProject.file(
            rootProject.file("compose-stability.conf")
          )
        )
        enableNonSkippingGroupOptimization.set(true)
      }
    }
  }

  plugins.withId("com.android.library") { setupAndroid() }
  plugins.withId("com.android.application") { setupAndroid() }

  tasks.withType<KotlinCompilationTask<*>> {
    compilerOptions.freeCompilerArgs.addAll(
      "-opt-in=kotlin.experimental.ExperimentalTypeInference",
      "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
      "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
      "-opt-in=androidx.compose.animation.core.InternalAnimationApi",
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
      "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
      "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
      "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi"
    )
  }
}
