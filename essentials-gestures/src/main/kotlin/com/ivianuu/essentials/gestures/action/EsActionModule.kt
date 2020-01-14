package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.gestures.action.actions.EsAppActionModule
import com.ivianuu.essentials.gestures.action.actions.EsAssistantActionModule
import com.ivianuu.essentials.gestures.action.actions.EsBackActionModule
import com.ivianuu.essentials.gestures.action.actions.EsBluetoothActionModule
import com.ivianuu.essentials.gestures.action.actions.EsHomeActionModule
import com.ivianuu.essentials.gestures.action.actions.EsLastAppActionModule
import com.ivianuu.essentials.gestures.action.actions.EsLockScreenActionModule
import com.ivianuu.essentials.gestures.action.actions.EsNotificationsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsPowerDialogActionModule
import com.ivianuu.essentials.gestures.action.actions.EsQuickSettingsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsRecentsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsScreenshotActionModule
import com.ivianuu.essentials.gestures.action.actions.EsSearchActionModule
import com.ivianuu.essentials.gestures.action.actions.EsShortcutActionModule
import com.ivianuu.essentials.gestures.action.actions.EsSplitScreenActionModule
import com.ivianuu.essentials.gestures.action.actions.EsTorchActionModule
import com.ivianuu.essentials.gestures.action.actions.EsVolumeActionModule
import com.ivianuu.essentials.gestures.action.actions.EsWifiActionModule
import com.ivianuu.injekt.Module

val EsActionModule = Module {
    map<String, Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()

    include(EsAppActionModule)
    include(EsAssistantActionModule)
    include(EsBackActionModule)
    include(EsBluetoothActionModule)
    include(EsHomeActionModule)
    include(EsLastAppActionModule)
    include(EsLockScreenActionModule)
    include(EsNotificationsActionModule)
    include(EsPowerDialogActionModule)
    include(EsQuickSettingsActionModule)
    include(EsRecentsActionModule)
    include(EsScreenshotActionModule)
    include(EsSearchActionModule)
    include(EsShortcutActionModule)
    include(EsSplitScreenActionModule)
    include(EsTorchActionModule)
    include(EsVolumeActionModule)
    include(EsWifiActionModule)
}
