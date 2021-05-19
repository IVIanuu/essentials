package com.ivianuu.essentials.android.settings

import android.provider.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AndroidSettingModule<T : S, S>(
  private val name: String,
  private val type: AndroidSettingsType
) {
  @Provide fun dataStore(
    adapter: AndroidSettingAdapter<T>,
    contentChangesFactory: ContentChangesFactory,
    dispatcher: IODispatcher,
    scope: InjectCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> = object : DataStore<T> {
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
          adapter.get()
        }
      }.shareIn(scope, SharingStarted.WhileSubscribed(), 1)
      .distinctUntilChanged()
    private val actor = scope.actor()
    override suspend fun updateData(transform: T.() -> T) = actor.actAndReply {
      val currentValue = adapter.get()
      val newValue = transform(currentValue)
      if (currentValue != newValue) adapter.set(newValue)
      newValue
    }
  }

  @Suppress("UNCHECKED_CAST")
  @Provide fun adapter(
    adapterFactory: (@Provide String, @Provide AndroidSettingsType, @Provide S) -> AndroidSettingAdapter<S>,
    initial: @Initial T
  ): AndroidSettingAdapter<T> = adapterFactory(name, type, initial) as AndroidSettingAdapter<T>
}

enum class AndroidSettingsType {
  GLOBAL, SECURE, SYSTEM
}
