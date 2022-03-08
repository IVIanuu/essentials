/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

import kotlin.reflect.*

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

inline fun <reified T> typeKeyOf(): TypeKey<T> = typeOf<T>().asTypeKey(null)

inline fun <reified T> typeKeyOf(arguments: Array<TypeKey<*>>): TypeKey<T> =
  typeOf<T>().asTypeKey(arguments)

fun <T> typeKeyOf(
  classifierFqName: String,
  isNullable: Boolean = false,
  arguments: Array<TypeKey<*>> = emptyArray()
): TypeKey<T> = TypeKey(
  classifierFqName = classifierFqName,
  isNullable = isNullable,
  arguments = arguments
)

@PublishedApi internal fun <T> KType.asTypeKey(arguments: Array<TypeKey<*>>?): TypeKey<T> {
  val finalArguments = arguments ?: arrayOfNulls<TypeKey<Any?>>(this.arguments.size).apply {
    for (index in this@asTypeKey.arguments.indices) {
      this[index] = this@asTypeKey.arguments[index].type?.asTypeKey(null)
        ?: typeKeyOf("*", isNullable = true)
    }
  }

  return TypeKey(
    classifierFqName = (classifier as? KClass<Any>)?.qualifiedName ?: "*",
    arguments = arguments as Array<TypeKey<*>>,
    isNullable = isMarkedNullable
  )
}
