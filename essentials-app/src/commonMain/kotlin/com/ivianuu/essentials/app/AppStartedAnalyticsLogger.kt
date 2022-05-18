/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.injekt.Provide

@Provide fun appStartedAnalyticsLogger(analytics: Analytics) = ScopeWorker<AppScope> {
  analytics.log("app_started")
}
