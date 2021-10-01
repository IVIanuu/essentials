package com.ivianuu.essentials.apps.shortcuts

import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Provide class AppShortcutRepository(
  private val broadcastsFactory: BroadcastsFactory,
  private val context: AppContext,
  private val dispatcher: IODispatcher
) {
  fun appShortcuts(packageName: String): Flow<List<AppShortcut>> = merge(
    broadcastsFactory(Intent.ACTION_PACKAGE_ADDED),
    broadcastsFactory(Intent.ACTION_PACKAGE_REMOVED),
    broadcastsFactory(Intent.ACTION_PACKAGE_CHANGED),
    broadcastsFactory(Intent.ACTION_PACKAGE_REPLACED)
  )
    .map { Unit }
    .onStart { emit(Unit) }
    .map {
      withContext(dispatcher) {
        val resources = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
          .resources
        parseAppShortcutMetadata(context, packageName)
          .flatMap { parseAppShortcuts(context, resources, packageName, it.key, it.value) }
      }
    }

  @Provide fun appShortcut(packageName: String, id: String): Flow<AppShortcut?> =
    appShortcuts(packageName)
      .map { it.single { it.id == id } }
}
