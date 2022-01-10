/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.work

import androidx.work.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.injekt.*

fun interface WorkInitializer : ScopeInitializer<AppScope>

@Provide fun workInitializer(
  context: AppContext,
  workerFactory: WorkerFactory
) = WorkInitializer {
  WorkManager.initialize(
    context,
    Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
  )
}
