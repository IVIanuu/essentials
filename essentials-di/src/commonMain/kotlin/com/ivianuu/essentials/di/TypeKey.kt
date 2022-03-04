/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class TypeKey<T> internal constructor(
  val classifierFqName: String,
  val isNullable: Boolean,
  val arguments: Array<TypeKey<*>>
) {
  private val hashCode = generateHashCode()

  override fun hashCode(): Int = hashCode

  override fun equals(other: Any?): Boolean = other is TypeKey<*> && hashCode == other.hashCode

  override fun toString(): String {
    val params = if (arguments.isNotEmpty()) {
      arguments.joinToString(
        separator = ", ",
        prefix = "<",
        postfix = ">"
      ) { it.toString() }
    } else {
      ""
    }

    return "Key(type=${classifierFqName}${if (isNullable) "?" else ""}$params)"
  }

  private fun generateHashCode(): Int {
    var result = classifierFqName.hashCode()
    result = 31 * result + arguments.contentHashCode()
    return result
  }
}

inline fun <reified T> typeKeyOf(): TypeKey<T> = typeOf<T>().asTypeKey()

fun <T> typeKeyOf(
  classifierFqName: String,
  isNullable: Boolean = false,
  arguments: Array<TypeKey<*>> = emptyArray()
): TypeKey<T> = TypeKey(
  classifierFqName = classifierFqName,
  isNullable = isNullable,
  arguments = arguments
)

@PublishedApi internal fun <T> KType.asTypeKey(): TypeKey<T> {
  val args = arrayOfNulls<TypeKey<Any?>>(arguments.size)

  for (index in arguments.indices) {
    args[index] = arguments[index].type?.asTypeKey() ?: typeKeyOf("*", isNullable = true)
  }

  return TypeKey(
    classifierFqName = (classifier as? KClass<Any>)?.qualifiedName ?: "*",
    arguments = args as Array<TypeKey<*>>,
    isNullable = isMarkedNullable
  )
}
