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
  api(Deps.AndroidX.Test.core)
  api(Deps.AndroidX.Test.junit)
  api(Deps.AndroidX.Test.rules)
  api(Deps.AndroidX.Test.runner)
  // compile(Deps.AndroidX.Ui.test)
  api(Deps.Injekt.core)
  api(Deps.Coroutines.test)
  api(Deps.junit)
  api(Deps.kotestAssertions)
  api(Deps.mockk)
  api(Deps.turbine)
}

plugins.apply("com.vanniktech.maven.publish")
