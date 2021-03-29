@file:Suppress("UnusedImport")

package com.ivianuu.essentials.sample

import android.app.Application
import com.ivianuu.essentials.about.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.activity.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.android.settings.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.coil.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.apppicker.*
import com.ivianuu.essentials.apps.ui.checkableapps.*
import com.ivianuu.essentials.backup.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.boot.*
import com.ivianuu.essentials.broadcast.*
import com.ivianuu.essentials.clipboard.*
import com.ivianuu.essentials.coil.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.foreground.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.actions.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.hidenavbar.*
import com.ivianuu.essentials.hidenavbar.unsupported.*
import com.ivianuu.essentials.moshi.*
import com.ivianuu.essentials.notificationlistener.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.permission.deviceadmin.*
import com.ivianuu.essentials.permission.ignorebatteryoptimizations.*
import com.ivianuu.essentials.permission.installunknownapps.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.essentials.permission.notificationlistener.*
import com.ivianuu.essentials.permission.packageusagestats.*
import com.ivianuu.essentials.permission.root.*
import com.ivianuu.essentials.permission.runtime.*
import com.ivianuu.essentials.permission.systemoverlay.*
import com.ivianuu.essentials.permission.ui.*
import com.ivianuu.essentials.permission.writesecuresettings.*
import com.ivianuu.essentials.permission.writesettings.*
import com.ivianuu.essentials.processrestart.*
import com.ivianuu.essentials.recentapps.*
import com.ivianuu.essentials.sample.tile.*
import com.ivianuu.essentials.sample.ui.*
import com.ivianuu.essentials.sample.work.*
import com.ivianuu.essentials.screenstate.*
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.shortcutpicker.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.systemoverlay.blacklist.*
import com.ivianuu.essentials.systemoverlay.*
import com.ivianuu.essentials.tile.*
import com.ivianuu.essentials.torch.*
import com.ivianuu.essentials.twilight.data.*
import com.ivianuu.essentials.twilight.domain.*
import com.ivianuu.essentials.twilight.ui.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.popup.*
import com.ivianuu.essentials.unlock.*
import com.ivianuu.essentials.util.*
import com.ivianuu.essentials.work.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.android.work.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*

class SampleApp : EsApp() {
    override fun buildAppGivenScope(): AppGivenScope =
        createAppGivenScope()
}