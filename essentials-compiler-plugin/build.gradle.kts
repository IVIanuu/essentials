/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.*

plugins {
  kotlin("jvm")
  kotlin("kapt")
  kotlin("plugin.serialization")
  id("com.github.johnrengelman.shadow")
  id("org.jetbrains.compose")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")

val shadowJar = tasks.getByName<ShadowJar>("shadowJar") {
  relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
  dependencies {
    exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
    exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common"))
    exclude(dependency("org.jetbrains:annotations"))

    exclude(dependency("com.intellij:openapi"))
    exclude(dependency("com.intellij:extensions"))
    exclude(dependency("com.intellij:annotations"))
  }
}

artifacts {
  archives(shadowJar)
}

dependencies {
  compileOnly(Deps.Kotlin.compilerEmbeddable)
  compileOnly(Deps.Injekt.compilerPlugin)
  compileOnly(Deps.Compose.compiler)
  implementation(Deps.autoService)
  kapt(Deps.autoService)
  testImplementation(Deps.Compose.compiler)
  testImplementation(Deps.Compose.runtime)
  testImplementation(Deps.Injekt.compilerPlugin)
  testImplementation(Deps.junit)
  testImplementation(Deps.Kotlin.compilerEmbeddable)
  testImplementation(Deps.kotlinCompileTesting)
  testImplementation(Deps.kotestAssertions)
  testImplementation(project(":essentials-core"))
  testImplementation(project(":essentials-coroutines"))
}

plugins.apply("com.vanniktech.maven.publish")
