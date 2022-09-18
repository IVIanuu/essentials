/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

import com.vanniktech.maven.publish.SonatypeHost

buildscript {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
    jcenter()
    maven("https://plugins.gradle.org/m2")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  dependencies {
    classpath(Deps.androidGradlePlugin)
    classpath(Deps.AtomicFu.gradlePlugin)
    classpath(Deps.dexcountGradlePlugin)
    classpath(Deps.dokkaGradlePlugin)
    classpath(Deps.essentialsGradlePlugin)
    classpath(Deps.Injekt.gradlePlugin)
    classpath(Deps.Kotlin.gradlePlugin)
    classpath(Deps.KotlinSerialization.gradlePlugin)
    classpath(Deps.mavenPublishGradlePlugin)
    classpath(Deps.shadowGradlePlugin)
  }
}

allprojects {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
  }

  plugins.withId("com.vanniktech.maven.publish") {
    extensions.getByType<com.vanniktech.maven.publish.MavenPublishPluginExtension>()
      .sonatypeHost = SonatypeHost.S01
  }

  /*tasks.withType<KotlinJvmCompile>().configureEach {
    kotlinOptions {
      jvmTarget = if (
        project.name == "essentials-gradle-plugin" ||
          project.name == "essentials-compiler-plugin") "11"
      else "1.8"
    }
  }**/
}
