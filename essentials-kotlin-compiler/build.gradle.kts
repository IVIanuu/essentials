plugins {
    id("com.github.ben-manes.versions")
    kotlin("jvm")
    kotlin("kapt")
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/java-8.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-compiler-args.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-lint.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

dependencies {
    compileOnly(Deps.Kotlin.compilerEmbeddable)
    implementation(Deps.Kotlin.stdlib)

    implementation(Deps.processingX)
    kapt(Deps.processingX)

    testImplementation(Deps.compileTesting)
    testImplementation(Deps.junit)
    testImplementation(Deps.Kotlin.compilerEmbeddable)
}