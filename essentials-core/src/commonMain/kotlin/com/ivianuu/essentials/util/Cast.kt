package com.ivianuu.essentials.util

inline fun <reified T> Any?.cast(): T = this as T

inline fun <reified T> Any?.safeAs(): T? = this as? T
