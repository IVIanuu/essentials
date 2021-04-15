package com.ivianuu.essentials.android.settings

import android.provider.Settings
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.actAndReply
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

class AndroidSettingModule<T : S, S>(
    private val name: String,
    private val type: AndroidSettingsType
) {
    @Given
    fun store(
        @Given adapter: AndroidSettingAdapter<T>,
        @Given contentChangesFactory: ContentChangesFactory,
        @Given dispatcher: IODispatcher,
        @Given scope: ScopeCoroutineScope<AppGivenScope>
    ): @Scoped<AppGivenScope> DataStore<T> = object : DataStore<T> {
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
            }.shareIn(scope, SharingStarted.Lazily, 1)
            .distinctUntilChanged()
        private val actor = scope.actor()
        override suspend fun updateData(transform: T.() -> T): T = actor.actAndReply {
            val currentValue = adapter.get()
            val newValue = transform(currentValue)
            if (currentValue != newValue) adapter.set(newValue)
            newValue
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Given
    fun adapter(
        @Given adapterFactory: (@Given String, @Given AndroidSettingsType, @Given S) -> AndroidSettingAdapter<S>,
        @Given initial: @Initial T
    ): AndroidSettingAdapter<T> = adapterFactory(name, type, initial) as AndroidSettingAdapter<T>
}

enum class AndroidSettingsType {
    GLOBAL, SECURE, SYSTEM
}
