package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ScreenLockRotation
import androidx.ui.material.icons.filled.ScreenRotation
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Module
private fun AutoRotationModule() {
    installIn<ApplicationComponent>()
    /*transient<@ActionQualifier("auto_rotation") Action> {
        resourceProvider: ResourceProvider ->
        Action(
            key = "auto_rotation",
            title =
        )
    }*/
    //bindAction<@ActionQualifier("auto_rotation") Action>()
    /*bindAction<@ActionQualifier("auto_rotation") Action>(
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
    )*/
    /*scoped<@AutoRotationSetting Box<Int>> {
        get<SettingsBoxFactory>()
            .int(Settings.System.ACCELEROMETER_ROTATION, SettingBox.Type.System, 1)
    }*/
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
