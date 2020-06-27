package com.ivianuu.essentials.store

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T> javaTypeOf() = typeOf<T>().asJavaType()

fun KType.asJavaType(): Type {
    return if (arguments.isNotEmpty()) {
        object : ParameterizedType {
            override fun getRawType(): Type {
                return ((this@asJavaType.classifier ?: Any::class) as KClass<*>).java
            }

            override fun getActualTypeArguments(): Array<Type> {
                return this@asJavaType.arguments
                    .map { it.type?.asJavaType() ?: Any::class.java }
                    .toTypedArray()
            }

            override fun getOwnerType(): Type? = null
        }
    } else {
        ((this@asJavaType.classifier ?: Any::class) as KClass<*>).java
    }
}
