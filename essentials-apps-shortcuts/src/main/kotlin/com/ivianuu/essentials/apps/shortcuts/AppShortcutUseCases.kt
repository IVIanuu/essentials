package com.ivianuu.essentials.apps.shortcuts

import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.util.PackageName
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

typealias AppShortcuts = List<AppShortcut>

@Provide fun appShortcuts(
  broadcastsFactory: BroadcastsFactory,
  context: AppContext,
  dispatcher: IODispatcher,
  packageName: PackageName
): Flow<AppShortcuts> = merge(
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

typealias AppShortcutId = String

@Provide fun appShortcut(
  appShortcuts: Flow<AppShortcuts>,
  id: AppShortcutId
): Flow<AppShortcut?> = appShortcuts
  .map { it.single { it.id == id } }
