package com.ivianuu.essentials.gestures.action.actions

import android.provider.Settings
import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ScreenLockRotation
import androidx.ui.material.icons.filled.ScreenRotation
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.android.settings.SettingDataStore
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.datastore.android.settings.int
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.gestures.action.getString
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.scoped
import com.ivianuu.injekt.unscoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Module
fun AutoRotationModule() {
    installIn<ApplicationComponent>()
    unscoped {
        Action(
            key = "auto_rotation",
            title = getString(R.string.es_action_auto_rotation),
            permissions = permissions { listOf(writeSettings) },
            unlockScreen = true,
            iconProvider = get<AutoRotationActionIconProvider>(),
            executor = get<AutoRotationActionExecutor>()
        ) as @StringKey("auto_rotation") Action
    }
    bindAction<@StringKey("auto_rotation") Action>()

    scoped<@AutoRotationSetting DataStore<Int>> {
        get<SettingsDataStoreFactory>()
            .int(Settings.System.ACCELEROMETER_ROTATION, SettingDataStore.Type.System, 1)
    }
}

@Unscoped
internal class AutoRotationActionExecutor(
    private val dataStore: @AutoRotationSetting DataStore<Int>
) : ActionExecutor {
    override suspend fun invoke() {
        dataStore.updateData { if (it != 1) 1 else 0 }
    }
}

@Unscoped
internal class AutoRotationActionIconProvider(
    private val dataStore: @AutoRotationSetting DataStore<Int>
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = dataStore.data
            .map { it == 1 }
            .map {
                if (it) Icons.Default.ScreenRotation
                else Icons.Default.ScreenLockRotation
            }
            .map { { Icon(it) } }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class AutoRotationSetting
