/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.analytics.log
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlin.reflect.KClass

@Provide fun screenLaunchAnalyticsLogger(
  analytics: Analytics,
  key: Key<*>,
  keyNames: Map<KClass<Key<*>>, TypeKey<Key<*>>>
) = ScopeWorker<KeyUiScope> {
  val keyName = keyNames[key::class]!!.value
  analytics.log("screen_launched") {
    put("key", keyName)
  }
}
