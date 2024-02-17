/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import de.robv.android.xposed.*
import kotlin.reflect.*

fun ClassLoader.getClass(className: String): KClass<*> =
  XposedHelpers.findClass(className, this).kotlin

fun ClassLoader.getClassOrNull(className: String): KClass<*>? =
  XposedHelpers.findClassIfExists(className, this)?.kotlin

fun <T : Any> KClass<T>.newInstance(
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): T = XposedHelpers.newInstance(java, parameterTypes, *args) as T

fun <T> Any.getField(fieldName: String): T = XposedHelpers.getObjectField(this, fieldName) as T

fun Any.setField(fieldName: String, value: Any?) {
  XposedHelpers.setObjectField(this, fieldName, value)
}

fun <T> KClass<*>.getStaticField(fieldName: String): T =
  XposedHelpers.getStaticObjectField(java, fieldName) as T

fun KClass<*>.setStaticField(fieldName: String, value: Any?) {
  XposedHelpers.setStaticObjectField(java, fieldName, value)
}

fun <T> Any.getAdditionalField(fieldName: String): T =
  XposedHelpers.getAdditionalInstanceField(this, fieldName) as T

fun Any.setAdditionalField(fieldName: String, value: Any?) {
  XposedHelpers.setAdditionalInstanceField(this, fieldName, value)
}

fun Any.removeAdditionalField(fieldName: String) {
  XposedHelpers.removeAdditionalInstanceField(this, fieldName)
}

fun <T> KClass<*>.getAdditionalStaticField(fieldName: String): T =
  XposedHelpers.getAdditionalStaticField(java, fieldName) as T

fun KClass<*>.setAdditionalStaticField(fieldName: String, value: Any?) {
  XposedHelpers.setAdditionalStaticField(java, fieldName, value)
}

fun KClass<*>.removeAdditionalStaticField(fieldName: String) {
  XposedHelpers.removeAdditionalStaticField(java, fieldName)
}

fun <T> Any.callMethod(
  methodName: String,
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): T =
  (if (parameterTypes == null) XposedHelpers.callMethod(this, methodName, *args)
  else XposedHelpers.callMethod(this, methodName, parameterTypes, *args)) as T

fun <T> KClass<*>.callStaticMethod(
  methodName: String,
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): T =
  (if (parameterTypes == null) XposedHelpers.callStaticMethod(java, methodName, *args)
  else XposedHelpers.callStaticMethod(java, methodName, parameterTypes, *args)) as T
