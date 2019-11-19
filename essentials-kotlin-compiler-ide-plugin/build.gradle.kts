plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version "0.4.9"
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")

intellij {
    pluginName = "essentials"
    //version = "2019.2"
    setPlugins("org.jetbrains.kotlin:1.3.41-release-IJ2019.2-1")
}

dependencies {
    implementation(project(":essentials-kotlin-compiler-hosted"))
}