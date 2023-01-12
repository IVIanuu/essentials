/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import java.io.File

/**
 * Run to regenerate system service modules
 * It's in this module because i was to lazy to create one
 */
fun main() {
  val workingDir = System.getProperty("user.dir")!!
  val file = File("$workingDir/essentials-android-core/src/main/kotlin")

  Class.forName("androidx.core.content.ContextCompat\$LegacyServiceMapHolder")
    .getDeclaredField("SERVICES")
    .also { it.isAccessible = true }
    .get(null)
    .cast<Map<Class<*>, String>>()
    .keys
    .toMutableSet()
    .apply {
      // is missing somehow
      add(CameraManager::class.java)
      // cannot hurt either:D
      add(PackageManager::class.java)
    }
    .groupBy { it.packageName }
    .mapValues { it.value.sortedBy { it.simpleName } }
    .toMutableMap()
    .forEach { (packageName, services) ->
      file.resolve(packageName.replace(".", "/"))
        .resolve("SystemServiceModule.kt")
        .run {
          parentFile!!.mkdirs()
          createNewFile()
          writeText(
            buildString {
              appendLine("package $packageName")
              appendLine("import android.content.Context")
              appendLine("import com.ivianuu.injekt.Provide")

              appendLine()
              appendLine("object SystemServiceModule {")
              services.forEach { service ->
                append(
                  "  @Provide fun ${service.simpleName.decapitalize()}(context: Context): " +
                      "${service.simpleName} =\n    context."
                )

                if (service == PackageManager::class.java) {
                  appendLine("packageManager")
                } else {
                  appendLine("getSystemService(${service.simpleName}::class.java)")
                }
              }

              appendLine("}")
            }
          )
        }
    }
}
