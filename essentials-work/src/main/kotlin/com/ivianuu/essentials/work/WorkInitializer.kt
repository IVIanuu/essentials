package com.ivianuu.essentials.work

import androidx.work.*
import com.ivianuu.essentials.app.ScopeInitializer
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

typealias WorkInitializer = ScopeInitializer<AppScope>

@Provide fun workInitializer(
  context: AppContext,
  workerFactory: WorkerFactory
): WorkInitializer = {
  WorkManager.initialize(
    context,
    Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
  )
}
