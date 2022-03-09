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

    return "${classifierFqName}$params${if (isNullable) "?" else ""}"
  }

  private fun generateHashCode(): Int {
    var result = classifierFqName.hashCode()
    result = 31 * result + arguments.contentHashCode()
    return result
  }
}

inline fun <reified T> classifierFqNameOf(): String = typeKeyOf<T>().classifierFqName

inline fun <reified T> typeKeyOf(): TypeKey<T> = typeOf<T>().asTypeKey()

@PublishedApi internal fun <T> KType.asTypeKey(): TypeKey<T> {
  val arguments = arrayOfNulls<TypeKey<Any?>>(this.arguments.size).apply {
    for (index in this@asTypeKey.arguments.indices) {
      this[index] = this@asTypeKey.arguments[index].type?.asTypeKey()
        ?: TypeKey("*", true, emptyArray())
    }
  }

  return TypeKey(
    classifierFqName = (classifier as? KClass<Any>)?.qualifiedName ?: "*",
    arguments = arguments as Array<TypeKey<*>>,
    isNullable = isMarkedNullable
  )
}
