/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.work

import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.ScopeInitializer
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject

fun interface WorkInitializer : ScopeInitializer<AppScope>

context(AppContext, WorkerFactory)
    @Provide fun workInitializer() = WorkInitializer {
  WorkManager.initialize(
    inject(),
    Configuration.Builder()
      .setWorkerFactory(inject())
      .build()
  )
}
