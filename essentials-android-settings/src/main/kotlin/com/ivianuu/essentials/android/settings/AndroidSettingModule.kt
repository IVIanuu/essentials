/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.settings

import android.content.ContentResolver
import android.provider.Settings
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IOContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

class AndroidSettingModule<T : S, S>(
  private val name: String,
  private val type: AndroidSettingsType,
  private val defaultValue: T
) {
  context(NamedCoroutineScope<AppScope>)
      @Suppress("UNCHECKED_CAST")
      @Provide fun dataStore(
    adapter: AndroidSettingAdapter<S>,
    contentChangesFactory: ContentChangesFactory,
    contentResolver: ContentResolver,
    context: IOContext
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
      .shareIn(inject(), SharingStarted.WhileSubscribed(), 1)
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
