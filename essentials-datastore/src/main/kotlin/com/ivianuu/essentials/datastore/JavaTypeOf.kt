/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.datastore

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
