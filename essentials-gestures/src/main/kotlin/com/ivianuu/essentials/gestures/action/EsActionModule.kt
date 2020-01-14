package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.gestures.action.actions.EsAppActionModule
import com.ivianuu.essentials.gestures.action.actions.EsBackActionModule
import com.ivianuu.essentials.gestures.action.actions.EsHomeActionModule
import com.ivianuu.essentials.gestures.action.actions.EsNotificationsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsPowerDialogActionModule
import com.ivianuu.essentials.gestures.action.actions.EsQuickSettingsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsRecentsActionModule
import com.ivianuu.essentials.gestures.action.actions.EsSplitScreenActionModule
import com.ivianuu.essentials.gestures.action.actions.EsVolumeActionModule
import com.ivianuu.essentials.gestures.action.actions.EsWifiActionModule
import com.ivianuu.injekt.Module

val EsActionModule = Module {
    map<String, Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()

    include(EsAppActionModule)
    include(EsBackActionModule)
    include(EsHomeActionModule)
    include(EsNotificationsActionModule)
    include(EsPowerDialogActionModule)
    include(EsQuickSettingsActionModule)
    include(EsRecentsActionModule)
    include(EsSplitScreenActionModule)
    include(EsVolumeActionModule)
    include(EsWifiActionModule)
}
