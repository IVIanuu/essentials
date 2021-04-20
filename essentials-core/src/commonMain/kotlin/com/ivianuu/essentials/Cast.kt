package com.ivianuu.essentials

inline fun <reified T> Any?.cast(): T = this as T

inline fun <reified T> Any?.safeAs(): T? = this as? T
