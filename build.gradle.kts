buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
        mavenCentral()
        maven("https://dl.bintray.com/ivianuu/maven/")
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/drummer-aidan/maven/")
    }

    dependencies {
        classpath(Deps.androidGradlePlugin)
        classpath(Deps.bintrayGradlePlugin)
        classpath(Deps.butterknifeGradlePlugin)
        classpath(Deps.kotlinGradlePlugin)
        classpath(Deps.mavenGradlePlugin)
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
        mavenCentral()
        maven("https://dl.bintray.com/ivianuu/maven/")
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/drummer-aidan/maven/")
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
