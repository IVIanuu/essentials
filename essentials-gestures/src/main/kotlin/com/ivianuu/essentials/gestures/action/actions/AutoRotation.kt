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
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.MergeInto
import kotlinx.coroutines.flow.map

@MergeInto(ApplicationComponent::class)
@Module
object EsAutoRotationModule {

    @ActionBinding
    fun autoRotationAction(
        autoRotationIcon: autoRotationIcon,
        choosePermissions: choosePermissions,
        setting: AutoRotationSetting,
        resources: Resources,
    ): Action = Action(
        key = "auto_rotation",
        title = resources.getString(R.string.es_action_auto_rotation),
        permissions = choosePermissions { listOf(writeSettings) },
        unlockScreen = true,
        icon = autoRotationIcon(),
        execute = { setting.updateData { if (it != 1) 1 else 0 } }
    )

    @Binding(ApplicationComponent::class)
    fun autoRotationSetting(factory: SettingsDataStoreFactory): AutoRotationSetting = factory
        .int(Settings.System.ACCELEROMETER_ROTATION, SettingDataStore.Type.System, 1)

}

@FunBinding
fun autoRotationIcon(setting: AutoRotationSetting): ActionIcon = setting.data
    .map { it == 1 }
    .map {
        if (it) R.drawable.es_ic_screen_rotation
        else R.drawable.es_ic_screen_lock_rotation
    }
    .map { { Icon(vectorResource(it)) } }

typealias AutoRotationSetting = DataStore<Int>
