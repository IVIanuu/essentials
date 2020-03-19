package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.gestures.action.actions.appAction
import com.ivianuu.essentials.gestures.action.actions.assistantAction
import com.ivianuu.essentials.gestures.action.actions.autoRotation
import com.ivianuu.essentials.gestures.action.actions.backAction
import com.ivianuu.essentials.gestures.action.actions.bluetoothAction
import com.ivianuu.essentials.gestures.action.actions.cameraAction
import com.ivianuu.essentials.gestures.action.actions.homeAction
import com.ivianuu.essentials.gestures.action.actions.inputMethodAction
import com.ivianuu.essentials.gestures.action.actions.lastAppAction
import com.ivianuu.essentials.gestures.action.actions.lockScreenAction
import com.ivianuu.essentials.gestures.action.actions.notificationsAction
import com.ivianuu.essentials.gestures.action.actions.powerDialogAction
import com.ivianuu.essentials.gestures.action.actions.quickSettingsAction
import com.ivianuu.essentials.gestures.action.actions.recentAppsAction
import com.ivianuu.essentials.gestures.action.actions.screenshotAction
import com.ivianuu.essentials.gestures.action.actions.searchAction
import com.ivianuu.essentials.gestures.action.actions.shortcutAction
import com.ivianuu.essentials.gestures.action.actions.splitScreenAction
import com.ivianuu.essentials.gestures.action.actions.torchAction
import com.ivianuu.essentials.gestures.action.actions.volumeAction
import com.ivianuu.essentials.gestures.action.actions.wifiAction
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.common.set

fun ComponentBuilder.esActions() {
    map<String, Action>()
    set<ActionFactory>()
    set<ActionPickerDelegate>()

    appAction()
    assistantAction()
    autoRotation()
    backAction()
    bluetoothAction()
    cameraAction()
    homeAction()
    inputMethodAction()
    lastAppAction()
    lockScreenAction()
    notificationsAction()
    powerDialogAction()
    quickSettingsAction()
    recentAppsAction()
    screenshotAction()
    searchAction()
    shortcutAction()
    splitScreenAction()
    torchAction()
    volumeAction()
    wifiAction()
}
