plugins {
    kotlin("jvm")
    id("com.ivianuu.essentials")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
//apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-lint.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

dependencies {
    compile(Deps.Coroutines.core)

    compile(project(":essentials-coroutines"))
    compile(project(":essentials-datastore"))
    compile(project(":essentials-moshi"))

    compile(Deps.Injekt.common)
    compile(Deps.Injekt.composition)
    compile(Deps.Injekt.core)

    compile(Deps.Kotlin.stdlib)

    compile(Deps.kotlinFlowExtensions)

    compile(Deps.kotlinResult)
}