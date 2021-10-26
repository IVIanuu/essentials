/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.android.settings

import android.content.ContentResolver
import android.provider.Settings
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.coroutines.ComponentScope
import com.ivianuu.injekt.coroutines.IODispatcher
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
  @Suppress("UNCHECKED_CAST")
  @Provide fun dataStore(
    adapter: AndroidSettingAdapter<S>,
    contentChangesFactory: ContentChangesFactory,
    contentResolver: ContentResolver,
    dispatcher: IODispatcher,
    scope: ComponentScope<AppComponent>
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
        withContext(dispatcher) {
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
