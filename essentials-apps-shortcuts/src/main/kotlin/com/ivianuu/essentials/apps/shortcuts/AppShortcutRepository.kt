/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.shortcuts

import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
