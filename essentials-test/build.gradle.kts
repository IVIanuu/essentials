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

plugins {
  kotlin("jvm")
  id("com.ivianuu.essentials")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")

dependencies {
  compile(Deps.AndroidX.Test.core)
  compile(Deps.AndroidX.Test.junit)
  compile(Deps.AndroidX.Test.rules)
  compile(Deps.AndroidX.Test.runner)
  // compile(Deps.AndroidX.Ui.test)
  compile(Deps.Injekt.core)
  compile(Deps.Coroutines.test)
  compile(Deps.junit)
  compile(Deps.kotestAssertions)
  compile(Deps.mockk)
  compile(Deps.turbine)
}

plugins.apply("com.vanniktech.maven.publish")
