package com.ivianuu.essentials.gestures.action.actions

import android.provider.Settings
import androidx.compose.foundation.Icon
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.android.settings.SettingDataStore
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.datastore.android.settings.int
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.map

object EsAutoRotationGivens {

    @GivenAction
    fun autoRotationAction() = Action(
        key = "auto_rotation",
        title = Resources.getString(R.string.es_action_auto_rotation),
        permissions = permissions { listOf(writeSettings) },
        unlockScreen = true,
        icon = autoRotationIcon(),
        execute = {
            given<AutoRotationSetting>()
                .updateData { if (it != 1) 1 else 0 }
        }
    )

    @Given(ApplicationContext::class)
    fun autoRotationSetting(): AutoRotationSetting = given<SettingsDataStoreFactory>()
        .int(Settings.System.ACCELEROMETER_ROTATION, SettingDataStore.Type.System, 1)

}

@Reader
private fun autoRotationIcon(): ActionIcon = given<AutoRotationSetting>().data
    .map { it == 1 }
    .map {
        if (it) R.drawable.es_ic_screen_rotation
        else R.drawable.es_ic_screen_lock_rotation
    }
    .map { { Icon(vectorResource(it)) } }

typealias AutoRotationSetting = DataStore<Int>
