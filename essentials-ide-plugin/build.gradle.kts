/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
  kotlin("jvm")
  id("org.jetbrains.intellij") version "1.1.4"
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")

intellij {
  version.set("2020.3.1")
  pluginName.set("Essentials ide plugin")
  updateSinceUntilBuild.set(false)
  plugins.addAll("org.jetbrains.kotlin:203-1.5.21-release-328-AS7717.8", "gradle", "gradle-java", "java")
  //localPath.set("/home/manu/android-studio")
}

tasks {
  instrumentCode {
    compilerVersion.set("201.7846.76")
  }
  runIde {
    jbrVersion.set("11_0_3b360.2")
  }
  buildSearchableOptions {
    jbrVersion.set("11_0_3b360.2")
  }
}

dependencies {
  api(project(":essentials-compiler-plugin", "shadow"))
}