/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Any?.cast(): T = this as T

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Any?.safeAs(): T? = this as? T
