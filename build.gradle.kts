/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

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
    classpath(Deps.Essentials.gradlePlugin)
    classpath(Deps.Kotlin.gradlePlugin)
    classpath(Deps.mavenPublishGradlePlugin)
    classpath(Deps.shadowGradlePlugin)
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
      signAllPublications()
    }
  }

  configurations.configureEach {
    resolutionStrategy.dependencySubstitution {
      substitute(module("com.ivianuu.essentials:ksp:${Deps.Essentials.version}")).using(project(":ksp"))
      substitute(module("com.ivianuu.essentials:compiler:${Deps.Essentials.version}")).using(project(":compiler"))
    }
  }
}
