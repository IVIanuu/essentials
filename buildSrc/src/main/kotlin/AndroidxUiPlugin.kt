import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidXUiPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            kotlinOptions.freeCompilerArgs +=
                    listOf("-P", "plugin:androidx.compose.plugins.idea:enabled=true")
        }
        project.plugins.all {
            when (this) {
                is LibraryPlugin -> {
                    val library = project.extensions.findByType(LibraryExtension::class.java)
                            ?: throw Exception("Failed to find Android extension")

                    library.defaultConfig.apply {
                        minSdkVersion(21)
                        targetSdkVersion(29)
                    }

                    library.lintOptions.apply {
                        // Too many Kotlin features require synthetic accessors - we want to rely on R8 to
                        // remove these accessors
                        disable("SyntheticAccessor")
                    }
                }
                is KotlinBasePluginWrapper -> {
                    val conf = project.configurations.create("kotlinPlugin")

                    project.tasks.withType(KotlinCompile::class.java).configureEach {
                        dependsOn(conf)
                        doFirst {
                            if (!conf.isEmpty) {
                                kotlinOptions.freeCompilerArgs +=
                                        "-Xplugin=${conf.files.first()}"
                            }
                        }
                    }
                }
            }
        }
    }
}