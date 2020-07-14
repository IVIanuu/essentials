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
import com.ivianuu.essentials.gestures.action.bindActionFactory
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object AutoRotationModule {

    @SetElements(ApplicationComponent::class)
    @Reader
    fun autoRotationAction() = bindAction {
        Action(
            key = "auto_rotation",
            title = Resources.getString(R.string.es_action_auto_rotation),
            permissions = permissions { listOf(writeSettings) },
            unlockScreen = true,
            iconProvider = given<AutoRotationActionIconProvider>(),
            executor = given<AutoRotationActionExecutor>()
        )
    }

    @Given(ApplicationComponent::class)
    @Reader
    fun autoRotationSetting(): AutoRotationSetting = given<SettingsDataStoreFactory>()
        .int(Settings.System.ACCELEROMETER_ROTATION, SettingDataStore.Type.System, 1)

}

@Given
@Reader
internal class AutoRotationActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        given<AutoRotationSetting>()
            .updateData { if (it != 1) 1 else 0 }
    }
}

@Given
@Reader
internal class AutoRotationActionIconProvider : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = given<AutoRotationSetting>().data
            .map { it == 1 }
            .map {
                if (it) Icons.Default.ScreenRotation
                else Icons.Default.ScreenLockRotation
            }
            .map { { Icon(it) } }
}

@Distinct
typealias AutoRotationSetting = DataStore<Int>
