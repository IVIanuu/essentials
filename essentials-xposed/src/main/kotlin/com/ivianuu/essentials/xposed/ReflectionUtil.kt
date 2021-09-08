package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.cast
import de.robv.android.xposed.XposedHelpers
import kotlin.reflect.KClass

fun ClassLoader.getClass(className: String): KClass<*> =
  XposedHelpers.findClass(className, this).kotlin

fun ClassLoader.getClassOrNull(className: String): KClass<*>? =
  XposedHelpers.findClassIfExists(className, this)?.kotlin

fun <T : Any> KClass<T>.newInstance(
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): T = XposedHelpers.newInstance(java, parameterTypes, *args) as T

@JvmName("getFieldT")
inline fun <reified T> Any.getField(fieldName: String): T = getField(fieldName).cast()

fun Any.getField(fieldName: String): Any? = XposedHelpers.getObjectField(this, fieldName)

fun Any.setField(fieldName: String, value: Any?) {
  XposedHelpers.setObjectField(this, fieldName, value)
}

@JvmName("getStaticFieldT")
inline fun <reified T> KClass<*>.getStaticField(fieldName: String): T =
  getStaticField(fieldName).cast()

fun KClass<*>.getStaticField(fieldName: String): Any? =
  XposedHelpers.getStaticObjectField(java, fieldName)

fun KClass<*>.setStaticField(fieldName: String, value: Any?) {
  XposedHelpers.setStaticObjectField(java, fieldName, value)
}

@JvmName("getAdditionalFieldT")
inline fun <reified T> Any.getAdditionalField(fieldName: String): T =
  getAdditionalField(fieldName).cast()

fun Any.getAdditionalField(fieldName: String): Any? =
  XposedHelpers.getAdditionalInstanceField(this, fieldName)

fun Any.setAdditionalField(fieldName: String, value: Any?) {
  XposedHelpers.setAdditionalInstanceField(this, fieldName, value)
}

fun Any.removeAdditionalField(fieldName: String) {
  XposedHelpers.removeAdditionalInstanceField(this, fieldName)
}

@JvmName("getAdditionalStaticFieldT")
inline fun <reified T> KClass<*>.getAdditionalStaticField(fieldName: String): T =
  getAdditionalStaticField(fieldName).cast()

fun KClass<*>.getAdditionalStaticField(fieldName: String): Any? =
  XposedHelpers.getAdditionalStaticField(java, fieldName)

fun KClass<*>.setAdditionalStaticField(fieldName: String, value: Any?) {
  XposedHelpers.setAdditionalStaticField(java, fieldName, value)
}

fun KClass<*>.removeAdditionalStaticField(fieldName: String) {
  XposedHelpers.removeAdditionalStaticField(java, fieldName)
}

@JvmName("callMethodT")
inline fun <reified T> Any.callMethod(
  methodName: String,
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): T = callMethod(methodName, *args, parameterTypes = parameterTypes).cast()

fun Any.callMethod(
  methodName: String,
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): Any? = if (parameterTypes == null) XposedHelpers.callMethod(this, methodName, *args)
else XposedHelpers.callMethod(this, methodName, parameterTypes, *args)

@JvmName("callStaticMethodT")
inline fun <reified T> KClass<*>.callStaticMethod(
  methodName: String,
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): T = callStaticMethod(methodName, *args, parameterTypes = parameterTypes).cast()

fun KClass<*>.callStaticMethod(
  methodName: String,
  vararg args: Any?,
  parameterTypes: Array<KClass<*>>? = null
): Any? = if (parameterTypes == null) XposedHelpers.callStaticMethod(java, methodName, *args)
else XposedHelpers.callStaticMethod(java, methodName, parameterTypes, *args)
