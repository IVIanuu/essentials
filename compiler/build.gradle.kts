/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp")
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
