/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.xposed

import android.annotation.*
import android.content.*
import essentials.*
import essentials.coroutines.*
import essentials.data.*
import essentials.util.*
import injekt.*
import de.robv.android.xposed.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

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
        catch { json.decodeFromString(serializer, serialized) }
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
    broadcastManager: BroadcastManager,
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
        catch { json.decodeFromString(serializer, serialized) }
          .getOrNull()
      } ?: default()
    }

    return broadcastManager.broadcasts(prefsChangedAction(config.modulePackageName))
      .filter { sharedPrefs.hasFileChanged() }
      .onStart<Any?> { emit(Unit) }
      .map { readData() }
      .distinctUntilChanged()
      .flowOn(coroutineContexts.io)
  }

  private fun prefsChangedAction(modulePackageName: String) =
    "${modulePackageName}.PREFS_CHANGED"
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class XposedPrefFlowTag
typealias XposedPrefFlow<T> = @XposedPrefFlowTag Flow<T>
