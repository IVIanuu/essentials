package com.ivianuu.essentials.gestures.action.actions

import android.provider.Settings
import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ScreenLockRotation
import androidx.ui.material.icons.filled.ScreenRotation
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.android.settings.SettingBox
import com.ivianuu.essentials.store.android.settings.SettingsBoxFactory
import com.ivianuu.essentials.store.android.settings.int
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Module
private fun AutoRotationModule() {
    installIn<ApplicationComponent>()
    action {
            resourceProvider: ResourceProvider,
            permissions: ActionPermissions,
            iconProvider: AutoRotationActionIconProvider,
            executor: AutoRotationActionExecutor ->
        Action(
            key = "auto_rotation",
            title = resourceProvider.getString(R.string.es_action_auto_rotation),
            permissions = listOf(permissions.writeSettings),
            unlockScreen = true,
            iconProvider = iconProvider,
            executor = executor
        ) as @StringKey("auto_rotation") Action
    }

    scoped<@AutoRotationSetting Box<Int>> { factory: SettingsBoxFactory ->
        factory.int(Settings.System.ACCELEROMETER_ROTATION, SettingBox.Type.System, 1)
    }
}

@Transient
internal class AutoRotationActionExecutor(
    private val box: @AutoRotationSetting Box<Int>
) : ActionExecutor {
    override suspend fun invoke() {
        box.updateData { if (it != 1) 1 else 0 }
    }
}

@Transient
internal class AutoRotationActionIconProvider(
    private val box: @AutoRotationSetting Box<Int>
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = box.data
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
