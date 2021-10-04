package com.ivianuu.essentials.xposed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.actAndReply
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.onFailure
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.scope.scoped
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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class XposedPrefModule<T : Any>(private val prefName: String, private val default: () -> T) {
  @SuppressLint("WorldReadableFiles")
  @Provide fun dataStore(
    appScope: AppScope,
    context: AppContext,
    dispatcher: IODispatcher,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    packageName: ModulePackageName,
    serializerFactory: () -> KSerializer<T>,
    scope: NamedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val sharedPrefs by lazy {
      context.getSharedPreferences(prefName, Context.MODE_WORLD_READABLE)
    }

    val json by lazy(jsonFactory)
    val serializer by lazy(serializerFactory)

    fun readData(): T {
      val serialized = sharedPrefs.getString("data", null)
      return serialized?.let {
        catch { json.decodeFromString(serializer, serialized) }
          .onFailure { it.printStackTrace() }
          .getOrNull()
      } ?: initial()
    }

    val data = callbackFlow<T> {
      val listener = scoped {
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
          scope.launch(dispatcher) {
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
      .flowOn(dispatcher)
      .shareIn(scope, SharingStarted.WhileSubscribed(), 1)

    val actor = scope.actor(dispatcher)

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
    dispatcher: IODispatcher,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    packageName: ModulePackageName,
    serializerFactory: () -> KSerializer<T>,
    coroutineScope: NamedCoroutineScope<AppScope>
  ): @Scoped<AppScope> XposedPrefFlow<T> {
    val sharedPrefs by lazy { XSharedPreferences(packageName, prefName) }

    val json by lazy(jsonFactory)
    val serializer by lazy(serializerFactory)

    fun readData(): T {
      sharedPrefs.reload()
      val serialized = sharedPrefs.getString("data", null)
      return serialized?.let {
        catch { json.decodeFromString(serializer, serialized) }
          .getOrNull()
      } ?: initial()
    }

    return broadcastsFactory(prefsChangedAction(packageName))
      .filter { sharedPrefs.hasFileChanged() }
      .map { Unit }
      .onStart { emit(Unit) }
      .map { readData() }
      .distinctUntilChanged()
      .flowOn(dispatcher)
      .shareIn(coroutineScope, SharingStarted.Lazily, 1)
  }

  @Provide
  fun initialOrDefault(initial: () -> @Initial T = default): @InitialOrDefault T = initial()

  private fun prefsChangedAction(packageName: ModulePackageName) =
    "${packageName}.PREFS_CHANGED"
}

typealias XposedPrefFlow<T> = Flow<T>
