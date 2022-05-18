/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.analytics

interface Analytics {
  fun log(name: String, params: Map<String, String> = emptyMap())
}

inline fun Analytics.log(name: String, builder: MutableMap<String, String>.() -> Unit) {
  log(name, buildMap(builder))
}
