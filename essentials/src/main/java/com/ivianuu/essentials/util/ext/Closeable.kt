package com.ivianuu.essentials.util.ext

import com.ivianuu.closeable.Closeable
import com.ivianuu.scopes.Scope

fun Closeable.closeBy(scope: Scope): Closeable = apply { scope.addListener { close() } }