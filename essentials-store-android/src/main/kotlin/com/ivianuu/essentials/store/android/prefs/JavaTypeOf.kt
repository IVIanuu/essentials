package com.ivianuu.essentials.store.android.prefs

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T> javaTypeOf() = typeOf<T>().asJavaType()

@PublishedApi
internal fun KType.asJavaType(): Type {
    return if (arguments.isNotEmpty()) {
        return object : ParameterizedType {
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
        return ((this@asJavaType.classifier ?: Any::class) as KClass<*>).java
    }
}
