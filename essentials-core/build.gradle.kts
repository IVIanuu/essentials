plugins {
    kotlin("jvm")
    id("com.github.ben-manes.versions")
    id("com.ivianuu.injekt")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-lint.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

dependencies {
    api(Deps.Coroutines.core)

    api(project(":essentials-coroutines"))
    api(project(":essentials-store"))

    api(Deps.Injekt.injekt)
    api(Deps.Injekt.common)

    api(Deps.Kotlin.stdlib)

    api(Deps.kotlinFlowExtensions)

    api(Deps.Scopes.scopes)
    api(Deps.Scopes.coroutines)
}