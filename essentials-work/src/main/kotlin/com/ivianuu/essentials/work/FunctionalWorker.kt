package com.ivianuu.essentials.work

import androidx.work.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.android.work.*
import java.util.*

@Provide @InjektWorker class FunctionalWorker(
  private val workers: Set<WorkerElement> = emptySet(),
  context: AppContext,
  workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
  override suspend fun doWork(): Result {
    val workers = workers
      .toMap()
      .mapKeys { it.key.value }
    val id = tags.first { it.startsWith(WORKER_ID_TAG_PREFIX) }
      .removePrefix(WORKER_ID_TAG_PREFIX)
    val scope = object : WorkScope {
      override val id: UUID
        get() = this@FunctionalWorker.id
      override val inputData: Data
        get() = this@FunctionalWorker.inputData
      override val runAttemptCount: Int
        get() = this@FunctionalWorker.runAttemptCount
      override val tags: Set<String>
        get() = this@FunctionalWorker.tags

      override suspend fun setForeground(foregroundInfo: ForegroundInfo) {
        this@FunctionalWorker.setForeground(foregroundInfo)
      }

      override suspend fun setProgress(data: Data) {
        this@FunctionalWorker.setProgress(data)
      }
    }
    val worker = workers[id]?.invoke() ?: error("No worker found for $id")
    return worker(scope)
  }
}
