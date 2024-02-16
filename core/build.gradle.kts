/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
plugins {
  id("com.android.library")
  id("com.ivianuu.essentials")
  kotlin("multiplatform")
}

android {
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
  jvm()
  android {
    publishLibraryVariants("release")
  }

  sourceSets {
    val commonJvmMain = create("commonJvmMain") {
      dependsOn(commonMain.get())
    }

    named("androidMain") {
      dependsOn(commonJvmMain)
      dependencies {
        api(Deps.Compose.foundation)
      }
    }

    commonMain {
      dependencies {
        api(Deps.Arrow.core)
        api(Deps.Arrow.fxCoroutines)
        api(Deps.Arrow.resilience)
        api(Deps.AtomicFu.runtime)
        api(Deps.Compose.runtime)
        api(Deps.Compose.runtimeSaveable)
        api(Deps.Coroutines.core)
        api(Deps.Injekt.core)
        api(Deps.Injekt.common)
        api(Deps.KotlinSerialization.json)
        api(Deps.quiver)
      }
    }

    named("jvmMain") {
      dependsOn(commonJvmMain)
    }

    named("jvmTest") {
      dependencies {
        implementation(project(":test"))
      }
    }
  }
}

plugins.apply("com.vanniktech.maven.publish")
