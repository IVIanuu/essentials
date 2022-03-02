/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

import com.vanniktech.maven.publish.*

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
    classpath(Deps.Compose.gradlePlugin)
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
}