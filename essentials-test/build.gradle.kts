/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  kotlin("jvm")
  id("com.ivianuu.essentials")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")

dependencies {
  api(Deps.AndroidX.Test.core)
  api(Deps.AndroidX.Test.junit)
  api(Deps.AndroidX.Test.rules)
  api(Deps.AndroidX.Test.runner)
  // compile(Deps.AndroidX.Ui.test)
  api(Deps.Coroutines.test)
  api(Deps.junit)
  api(Deps.kotestAssertions)
  api(Deps.mockk)
  api(Deps.turbine)
}

plugins.apply("com.vanniktech.maven.publish")
