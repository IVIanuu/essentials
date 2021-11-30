/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.settings

import android.content.*
import android.provider.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AndroidSettingModule<T : S, S>(
  private val name: String,
  private val type: AndroidSettingsType,
  private val defaultValue: T
) {
  @Suppress("UNCHECKED_CAST")
  @Provide fun dataStore(
    adapter: AndroidSettingAdapter<S>,
    contentChangesFactory: ContentChangesFactory,
    contentResolver: ContentResolver,
    context: IOContext,
    scope: NamedCoroutineScope<AppScope>
  ): DataStore<T> = object : DataStore<T> {
    override val data: Flow<T> = contentChangesFactory(
      when (type) {
        AndroidSettingsType.GLOBAL -> Settings.Global.getUriFor(name)
        AndroidSettingsType.SECURE -> Settings.Secure.getUriFor(name)
        AndroidSettingsType.SYSTEM -> Settings.System.getUriFor(name)
      }
    )
      .onStart { emit(Unit) }
      .map {
        withContext(context) {
          adapter.get(contentResolver, name, type, defaultValue) as T
        }
      }
      .shareIn(scope, SharingStarted.WhileSubscribed(), 1)
      .distinctUntilChanged()

    override suspend fun updateData(transform: T.() -> T): T {
      val currentValue = adapter.get(contentResolver, name, type, defaultValue) as T
      val newValue = transform(currentValue)
      if (currentValue != newValue)
        adapter.set(contentResolver, name, type, newValue)
      return newValue
    }
  }
}

enum class AndroidSettingsType {
  GLOBAL, SECURE, SYSTEM
}
