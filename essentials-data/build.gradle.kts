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
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.ivianuu.essentials")
  id("org.jetbrains.compose")
}

kotlin {
  jvm()

  sourceSets {
    commonMain {
      dependencies {
        api(project(":essentials-db"))
        api(project(":essentials-resource"))
        api(project(":essentials-tuples"))
        api(project(":essentials-serialization"))
        api(project(":essentials-state"))
        api(compose.runtime)
        api(Deps.Coroutines.core)
        api(Deps.Injekt.core)
        api(Deps.Injekt.common)
      }
    }
    named("jvmTest") {
      dependencies {
        implementation(project(":essentials-test"))
      }
    }
  }
}

plugins.apply("com.vanniktech.maven.publish")

