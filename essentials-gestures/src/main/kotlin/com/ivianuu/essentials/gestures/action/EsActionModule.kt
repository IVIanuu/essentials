package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.gestures.action.actions.EsAppActionModule
import com.ivianuu.essentials.gestures.action.actions.EsAssistantActionModule
import com.ivianuu.essentials.gestures.action.actions.EsBackActionModule
import com.ivianuu.essentials.gestures.action.actions.EsBluetoothActionModule
import com.ivianuu.essentials.gestures.action.actions.EsCameraActionModule
import com.ivianuu.essentials.gestures.action.actions.EsHomeActionModule
import com.ivianuu.essentials.gestures.action.actions.EsInputMethodActionModule
import com.ivianuu.essentials.gestures.action.actions.EsLastAppActionModule
import com.ivianuu.essentials.gestures.action.actions.EsLockScreenActionModule
import com.ivianuu.essentials.gestures.action.actions.EsNotificationsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsPowerDialogActionModule
import com.ivianuu.essentials.gestures.action.actions.EsQuickSettingsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsRecentsAppsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsScreenshotActionModule
import com.ivianuu.essentials.gestures.action.actions.EsSearchActionModule
import com.ivianuu.essentials.gestures.action.actions.EsShortcutActionModule
import com.ivianuu.essentials.gestures.action.actions.EsSplitScreenActionModule
import com.ivianuu.essentials.gestures.action.actions.EsTorchActionModule
import com.ivianuu.essentials.gestures.action.actions.EsVolumeActionModule
import com.ivianuu.essentials.gestures.action.actions.EsWifiActionModule
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.common.set

val EsActionModule = Module {
    map<String, Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()

    modules(EsAppActionModule)
    modules(EsAssistantActionModule)
    modules(EsBackActionModule)
    modules(EsBluetoothActionModule)
    modules(EsCameraActionModule)
    modules(EsHomeActionModule)
    modules(EsInputMethodActionModule)
    modules(EsLastAppActionModule)
    modules(EsLockScreenActionModule)
    modules(EsNotificationsActionModule)
    modules(EsPowerDialogActionModule)
    modules(EsQuickSettingsActionModule)
    modules(EsRecentsAppsActionModule)
    modules(EsScreenshotActionModule)
    modules(EsSearchActionModule)
    modules(EsShortcutActionModule)
    modules(EsSplitScreenActionModule)
    modules(EsTorchActionModule)
    modules(EsVolumeActionModule)
    modules(EsWifiActionModule)
}
