/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import arrow.core.Either
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.printErrors
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import de.robv.android.xposed.XSharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class XposedPrefModule<T : Any>(private val prefName: String, private val default: () -> T) {
  @SuppressLint("WorldReadableFiles")
  @Provide fun dataStore(
    appContext: AppContext,
    config: XposedConfig,
    coroutineContexts: CoroutineContexts,
    coroutineScope: ScopedCoroutineScope<AppScope>,
    jsonFactory: () -> Json,
    scope: Scope<AppScope>,
    serializerFactory: () -> KSerializer<T>
  ): @Scoped<AppScope> DataStore<T> {
    val sharedPrefs by lazy {
      appContext.getSharedPreferences(
        prefName,
        Context.MODE_WORLD_READABLE
      )
    }

    val json by lazy(jsonFactory)
    val serializer by lazy(serializerFactory)

    fun readData(): T {
      val serialized = sharedPrefs.getString("data", null)
      return serialized?.let {
        Either.catch { json.decodeFromString(serializer, serialized) }
          .printErrors()
          .getOrNull()
      } ?: default()
    }

    val mutex = Mutex()

    return object : DataStore<T> {
      override val data: Flow<T> = callbackFlow {
        val listener = scope.scoped {
          SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            launch(coroutineContexts.io) {
              appContext.sendBroadcast(Intent(prefsChangedAction(config.modulePackageName)))
              send(readData())
            }
          }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
      }
        .onStart { emit(readData()) }
        .distinctUntilChanged()
        .flowOn(coroutineContexts.io)
        .shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 1)

      @SuppressLint("ApplySharedPref")
      override suspend fun updateData(transform: T.() -> T): T = mutex.withLock {
        with(coroutineContexts.io) {
          val previousData = readData()
          val newData = transform(previousData)
          sharedPrefs.edit()
            .putString("data", json.encodeToString(serializer, newData))
            .commit()
          newData
        }
      }
    }
  }

  @Provide fun xposedPrefFlow(
    broadcastsFactory: BroadcastsFactory,
    config: XposedConfig,
    coroutineContexts: CoroutineContexts,
    jsonFactory: () -> Json,
    serializerFactory: () -> KSerializer<T>
  ): XposedPrefFlow<T> {
    val sharedPrefs by lazy { XSharedPreferences(config.modulePackageName, prefName) }

    val json by lazy(jsonFactory)
    val serializer by lazy(serializerFactory)

    fun readData(): T {
      sharedPrefs.reload()
      val serialized = sharedPrefs.getString("data", null)
      return serialized?.let {
        Either.catch { json.decodeFromString(serializer, serialized) }
          .getOrNull()
      } ?: default()
    }

    return broadcastsFactory(prefsChangedAction(config.modulePackageName))
      .filter { sharedPrefs.hasFileChanged() }
      .onStart<Any?> { emit(Unit) }
      .map { readData() }
      .distinctUntilChanged()
      .flowOn(coroutineContexts.io)
  }

  private fun prefsChangedAction(modulePackageName: String) =
    "${modulePackageName}.PREFS_CHANGED"
}

@Tag annotation class XposedPrefFlowTag
typealias XposedPrefFlow<T> = @XposedPrefFlowTag Flow<T>
