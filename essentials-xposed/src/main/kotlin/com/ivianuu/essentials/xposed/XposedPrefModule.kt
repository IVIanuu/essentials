/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import android.annotation.*
import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import de.robv.android.xposed.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class XposedPrefModule<T : Any>(private val prefName: String, private val default: () -> T) {
  @SuppressLint("WorldReadableFiles")
  @Provide fun dataStore(
    context: AppContext,
    coroutineContext: IOContext,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    packageName: ModulePackageName,
    serializerFactory: () -> KSerializer<T>,
    scope: NamedCoroutineScope<AppScope>,
    appScope: Scope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val sharedPrefs by lazy {
      context.getSharedPreferences(prefName, Context.MODE_WORLD_READABLE)
    }

    val json by lazy(jsonFactory)
    val serializer by lazy(serializerFactory)

    fun readData(): T {
      val serialized = sharedPrefs.getString("data", null)
      return serialized?.let {
        runCatching { json.decodeFromString(serializer, serialized) }
          .onFailure { it.printStackTrace() }
          .getOrNull()
      } ?: initial()
    }

    val data = callbackFlow<T> {
      val listener = appScope {
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
          scope.launch(coroutineContext) {
            context.sendBroadcast(Intent(prefsChangedAction(packageName)))
            send(readData())
          }
        }
      }
      sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
      awaitClose { sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
      .onStart { emit(readData()) }
      .distinctUntilChanged()
      .flowOn(coroutineContext)
      .shareIn(scope, SharingStarted.WhileSubscribed(), 1)

    val actor = scope.actor(coroutineContext)

    return object : DataStore<T> {
      override val data: Flow<T>
        get() = data

      @SuppressLint("ApplySharedPref")
      override suspend fun updateData(transform: T.() -> T): T = actor.actAndReply<T> {
        val previousData = readData()
        val newData = transform(previousData)
        sharedPrefs.edit()
          .putString("data", json.encodeToString(serializer, newData))
          .commit()
        newData
      }
    }
  }

  @Provide fun xposedPrefFlow(
    broadcastsFactory: BroadcastsFactory,
    coroutineContext: IOContext,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    packageName: ModulePackageName,
    serializerFactory: () -> KSerializer<T>
  ): XposedPrefFlow<T> {
    val sharedPrefs by lazy { XSharedPreferences(packageName.value, prefName) }

    val json by lazy(jsonFactory)
    val serializer by lazy(serializerFactory)

    fun readData(): T {
      sharedPrefs.reload()
      val serialized = sharedPrefs.getString("data", null)
      return serialized?.let {
        runCatching { json.decodeFromString(serializer, serialized) }
          .getOrElse { null }
      } ?: initial()
    }

    return broadcastsFactory(prefsChangedAction(packageName))
      .filter { sharedPrefs.hasFileChanged() }
      .onStart<Any?> { emit(Unit) }
      .map { readData() }
      .distinctUntilChanged()
      .flowOn(coroutineContext)
  }

  @Provide
  fun initialOrDefault(initial: () -> @Initial T = default): @InitialOrDefault T = initial()

  private fun prefsChangedAction(packageName: ModulePackageName) =
    "${packageName}.PREFS_CHANGED"
}

@Tag annotation class XposedPrefFlowTag
typealias XposedPrefFlow<T> = @XposedPrefFlowTag Flow<T>
