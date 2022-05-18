/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.injekt.Provide

@Provide fun appLaunchedAnalyticsLogger(analytics: Analytics) = ScopeWorker<UiScope> {
  analytics.log("app_launched")
}
