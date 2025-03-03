/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import android.content.*
import android.provider.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AndroidSettingModule<T : Any>(
  private val name: String,
  private val type: AndroidSettingsType,
  private val defaultValue: T
) {
  @Provide fun dataStore(
    contentChangeManager: ContentChangeManager,
    contentResolver: ContentResolver,
    coroutineContexts: CoroutineContexts,
    scope: ScopedCoroutineScope<AppScope>
  ): DataStore<T> = object : DataStore<T> {
    private suspend fun get(): T = withContext(coroutineContexts.io) {
      when (defaultValue) {
        is Float -> when (type) {
          AndroidSettingsType.GLOBAL -> Settings.Global.getFloat(contentResolver, name, defaultValue)
          AndroidSettingsType.SECURE -> Settings.Secure.getFloat(contentResolver, name, defaultValue)
          AndroidSettingsType.SYSTEM -> Settings.System.getFloat(contentResolver, name, defaultValue)
        }
        is Int -> when (type) {
          AndroidSettingsType.GLOBAL -> Settings.Global.getInt(contentResolver, name, defaultValue)
          AndroidSettingsType.SECURE -> Settings.Secure.getInt(contentResolver, name, defaultValue)
          AndroidSettingsType.SYSTEM -> Settings.System.getInt(contentResolver, name, defaultValue)
        }
        is Long -> when (type) {
          AndroidSettingsType.GLOBAL -> Settings.Global.getLong(contentResolver, name, defaultValue)
          AndroidSettingsType.SECURE -> Settings.Secure.getLong(contentResolver, name, defaultValue)
          AndroidSettingsType.SYSTEM -> Settings.System.getLong(contentResolver, name, defaultValue)
        }
        is String -> when (type) {
          AndroidSettingsType.GLOBAL -> Settings.Global.getString(contentResolver, name) ?: defaultValue
          AndroidSettingsType.SECURE -> Settings.Secure.getString(contentResolver, name) ?: defaultValue
          AndroidSettingsType.SYSTEM -> Settings.System.getString(contentResolver, name) ?: defaultValue
        }
        else -> throw AssertionError("Unsupported type ${defaultValue::class}")
      }.unsafeCast()
    }

    override val data: Flow<T> = contentChangeManager.contentChanges(
      when (type) {
        AndroidSettingsType.GLOBAL -> Settings.Global.getUriFor(name)
        AndroidSettingsType.SECURE -> Settings.Secure.getUriFor(name)
        AndroidSettingsType.SYSTEM -> Settings.System.getUriFor(name)
      }
    )
      .onStart { emit(Unit) }
      .map { get() }
      .shareIn(scope, SharingStarted.WhileSubscribed(), 1)
      .distinctUntilChanged()

    override suspend fun updateData(transform: T.() -> T): T {
      val currentValue = get()
      val newValue = transform(currentValue)
      if (currentValue != newValue)
        withContext(coroutineContexts.io) {
          when (newValue) {
            is Float -> when (type) {
              AndroidSettingsType.GLOBAL -> Settings.Global.putFloat(contentResolver, name, newValue)
              AndroidSettingsType.SECURE -> Settings.Secure.putFloat(contentResolver, name, newValue)
              AndroidSettingsType.SYSTEM -> Settings.System.putFloat(contentResolver, name, newValue)
            }
            is Int -> when (type) {
              AndroidSettingsType.GLOBAL -> Settings.Global.putInt(contentResolver, name, newValue)
              AndroidSettingsType.SECURE -> Settings.Secure.putInt(contentResolver, name, newValue)
              AndroidSettingsType.SYSTEM -> Settings.System.putInt(contentResolver, name, newValue)
            }
            is Long -> when (type) {
              AndroidSettingsType.GLOBAL -> Settings.Global.putLong(contentResolver, name, newValue)
              AndroidSettingsType.SECURE -> Settings.Secure.putLong(contentResolver, name, newValue)
              AndroidSettingsType.SYSTEM -> Settings.System.putLong(contentResolver, name, newValue)
            }
            is String -> when (type) {
              AndroidSettingsType.GLOBAL -> Settings.Global.putString(contentResolver, name, newValue)
              AndroidSettingsType.SECURE -> Settings.Secure.putString(contentResolver, name, newValue)
              AndroidSettingsType.SYSTEM -> Settings.System.putString(contentResolver, name, newValue)
            }
            else -> throw AssertionError("Unsupported type ${newValue::class}")
          }
        }
      return newValue
    }
  }
}

enum class AndroidSettingsType { GLOBAL, SECURE, SYSTEM }
