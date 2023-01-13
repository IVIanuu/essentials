/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Run to regenerate system service modules
 * It's in this module because i was to lazy to create one
 */
fun main() {
  val workingDir = System.getProperty("user.dir")!!
  val file = File("$workingDir/essentials-android-core/src/main/kotlin")

  // we have to change the sdk int because otherwise not all services will be generated
  null.updatePrivateFinalField<Int>(
    Build.VERSION::class,
    "SDK_INT"
  ) { Int.MAX_VALUE }

  Class.forName("androidx.core.content.ContextCompat\$LegacyServiceMapHolder")
    .getDeclaredField("SERVICES")
    .also { it.isAccessible = true }
    .get(null)
    .cast<Map<Class<*>, String>>()
    .keys
    .toMutableSet()
    .apply {
      // cannot hurt either:D
      add(PackageManager::class.java)
    }
    .groupBy { it.canonicalName!!.removeSuffix(".${it.simpleName}") }
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
              appendLine()
              appendLine("import android.content.Context")
              appendLine("import com.ivianuu.injekt.Provide")

              appendLine()
              appendLine("object SystemServiceModule {")
              services.forEach { service ->
                append(
                  "  @Provide inline fun ${service.simpleName.decapitalize()}(context: Context): " +
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

private fun <T> Any?.updatePrivateFinalField(
  clazz: KClass<*>,
  fieldName: String,
  transform: T.() -> T
): T {
  val field = clazz.java.declaredFields
    .single { it.name == fieldName }
  field.isAccessible = true
  val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
  modifiersField.isAccessible = true
  modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
  val currentValue = field.get(this)
  val newValue = transform(currentValue as T)
  field.set(this, newValue)
  return newValue
}