package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.gestures.action.actions.esAppActionBindings
import com.ivianuu.essentials.gestures.action.actions.esAssistantActionBindings
import com.ivianuu.essentials.gestures.action.actions.esBackActionBindings
import com.ivianuu.essentials.gestures.action.actions.esBluetoothActionBindings
import com.ivianuu.essentials.gestures.action.actions.esCameraActionBindings
import com.ivianuu.essentials.gestures.action.actions.esHomeActionBindings
import com.ivianuu.essentials.gestures.action.actions.esInputMethodActionBindings
import com.ivianuu.essentials.gestures.action.actions.esLastAppActionBindings
import com.ivianuu.essentials.gestures.action.actions.esLockScreenActionBindings
import com.ivianuu.essentials.gestures.action.actions.esNotificationsActionBindings
import com.ivianuu.essentials.gestures.action.actions.esPowerDialogActionBindings
import com.ivianuu.essentials.gestures.action.actions.esQuickSettingsActionBindings
import com.ivianuu.essentials.gestures.action.actions.esRecentsAppsActionBindings
import com.ivianuu.essentials.gestures.action.actions.esScreenshotActionBindings
import com.ivianuu.essentials.gestures.action.actions.esSearchActionBindings
import com.ivianuu.essentials.gestures.action.actions.esShortcutActionBindings
import com.ivianuu.essentials.gestures.action.actions.esSplitScreenActionBindings
import com.ivianuu.essentials.gestures.action.actions.esTorchActionBindings
import com.ivianuu.essentials.gestures.action.actions.esVolumeActionBindings
import com.ivianuu.essentials.gestures.action.actions.esWifiActionBindings
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.common.set

fun ComponentBuilder.esActionBindings() {
    map<String, Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()

    esAppActionBindings()
    esAssistantActionBindings()
    esBackActionBindings()
    esBluetoothActionBindings()
    esCameraActionBindings()
    esHomeActionBindings()
    esInputMethodActionBindings()
    esLastAppActionBindings()
    esLockScreenActionBindings()
    esNotificationsActionBindings()
    esPowerDialogActionBindings()
    esQuickSettingsActionBindings()
    esRecentsAppsActionBindings()
    esScreenshotActionBindings()
    esSearchActionBindings()
    esShortcutActionBindings()
    esSplitScreenActionBindings()
    esTorchActionBindings()
    esVolumeActionBindings()
    esWifiActionBindings()
}
