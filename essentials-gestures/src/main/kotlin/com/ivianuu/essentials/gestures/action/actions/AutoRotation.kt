package com.ivianuu.essentials.gestures.action.actions

import android.provider.Settings
import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ScreenLockRotation
import androidx.ui.material.icons.filled.ScreenRotation
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.android.settings.SettingBox
import com.ivianuu.essentials.store.android.settings.SettingsBoxFactory
import com.ivianuu.essentials.store.android.settings.int
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.get
import com.ivianuu.injekt.single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ApplicationScope
@Module
private fun ComponentBuilder.autoRotation() {
    action(
        key = "auto_rotation",
        title = { getStringResource(R.string.es_action_auto_rotation) },
        iconProvider = { get<AutoRotationActionIconProvider>() },
        unlockScreen = { true },
        executor = { get<AutoRotationActionExecutor>() },
        permissions = {
            listOf(
                actionPermission {
                    writeSettings
                }
            )
        }
    )
    single<Box<Int>>(qualifier = AutoRotationSetting) {
        get<SettingsBoxFactory>()
            .int(Settings.System.ACCELEROMETER_ROTATION, SettingBox.Type.System, 1)
    }
}

@Factory
private class AutoRotationActionExecutor(
    @AutoRotationSetting private val box: Box<Int>
) : ActionExecutor {
    override suspend fun invoke() {
        box.updateData { if (it != 1) 1 else 0 }
    }
}

@Factory
private class AutoRotationActionIconProvider(
    @AutoRotationSetting private val box: Box<Int>
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

@QualifierMarker
private annotation class AutoRotationSetting {
    companion object : Qualifier.Element
}
