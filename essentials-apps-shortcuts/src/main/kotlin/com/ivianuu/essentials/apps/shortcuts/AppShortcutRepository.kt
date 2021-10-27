package com.ivianuu.essentials.apps.shortcuts

import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface AppShortcutRepository {
  fun appShortcuts(packageName: String): Flow<List<AppShortcut>>

  fun appShortcut(packageName: String, id: String): Flow<AppShortcut?>
}

@Provide class AppShortcutRepositoryImpl(
  private val broadcastsFactory: BroadcastsFactory,
  private val context: AppContext,
  private val dispatcher: IODispatcher
) : AppShortcutRepository {
  override fun appShortcuts(packageName: String): Flow<List<AppShortcut>> = broadcastsFactory(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED,
    Intent.ACTION_PACKAGE_CHANGED,
    Intent.ACTION_PACKAGE_REPLACED
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      withContext(dispatcher) {
        val resources = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
          .resources
        parseAppShortcutMetadata(context, packageName)
          .flatMap { parseAppShortcuts(context, resources, packageName, it.key, it.value) }
      }
    }

  override fun appShortcut(packageName: String, id: String): Flow<AppShortcut?> =
    appShortcuts(packageName)
      .map { it.single { it.id == id } }
}
