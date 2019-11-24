plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    kotlin("kapt")
    id("de.fuerstenau.buildconfig")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

gradlePlugin {
    plugins {
        create("essentialsPlugin") {
            id = "com.ivianuu.essentials.kotlin.compiler"
            implementationClass =
                "com.ivianuu.essentials.kotlin.compiler.gradle.EssentialsGradlePlugin"
        }
    }
}

buildConfig {
    clsName = "BuildConfig"
    packageName = "com.ivianuu.essentials.kotlin.compiler"

    version = Publishing.version
    buildConfigField("String", "GROUP_ID", Publishing.groupId)
    buildConfigField("String", "ARTIFACT_ID", "essentials-kotlin-compiler")
}

dependencies {
    api(Deps.autoService)
    kapt(Deps.autoService)
    api(Deps.Kotlin.gradlePluginApi)
}