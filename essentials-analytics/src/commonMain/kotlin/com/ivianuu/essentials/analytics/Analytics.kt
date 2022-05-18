/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.analytics

import com.ivianuu.injekt.Provide

interface Analytics {
  fun log(name: String, params: Map<String, String> = emptyMap())

  fun setUserProperty(name: String, value: String)
}

@Provide object NoopAnalytics : Analytics {
  override fun log(name: String, params: Map<String, String>) {
  }

  override fun setUserProperty(name: String, value: String) {
  }
}

inline fun Analytics.log(name: String, builder: MutableMap<String, String>.() -> Unit) {
  log(name, buildMap(builder))
}
