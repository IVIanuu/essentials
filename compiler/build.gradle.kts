/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.*

plugins {
  kotlin("jvm")
  id("com.github.johnrengelman.shadow")
  id("com.google.devtools.ksp")
}

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
  compileOnly(Deps.Injekt.compiler)
  implementation(Deps.AutoService.annotations)
  ksp(Deps.AutoService.processor)
  testImplementation(Deps.Injekt.compiler)
  testImplementation(Deps.junit)
  testImplementation(Deps.Kotlin.compilerEmbeddable)
  testImplementation(Deps.KotlinCompileTesting.kotlinCompileTesting)
  testImplementation(Deps.kotestAssertions)
}

plugins.apply("com.vanniktech.maven.publish")
