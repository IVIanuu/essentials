package com.ivianuu.essentials.android.settings

import android.provider.Settings
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.ValueAction.*
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.effectOn
import com.ivianuu.essentials.util.ContentChangesFactory
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class AndroidSettingStoreModule<T : S, S>(
    private val name: String,
    private val type: AndroidSettingsType
) {
    @Given
    fun store(
        @Given adapter: AndroidSettingAdapter<T>,
        @Given contentChangesFactory: ContentChangesFactory,
        @Given dispatcher: IODispatcher
    ): StoreBuilder<AppGivenScope, T, ValueAction<T>> = {
        contentChangesFactory(
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
            }
            .update { it }
        effectOn<Update<T>> { action ->
            val currentValue = adapter.get()
            val newValue = action.transform(currentValue)
            if (currentValue != newValue) adapter.set(newValue)
            action.complete(newValue)
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
