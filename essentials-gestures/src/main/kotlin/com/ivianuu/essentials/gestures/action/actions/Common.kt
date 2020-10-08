package com.ivianuu.essentials.gestures.action.actions

import android.app.PendingIntent
import android.content.Intent
import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.shell.runShellCommand
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.flow.flowOf

internal fun coilActionIcon(data: Any): ActionIcon = flowOf { CoilImage(data = data) }

internal fun singleActionIcon(icon: @Composable () -> Unit): ActionIcon = flowOf(icon)

internal fun singleActionIcon(icon: VectorAsset) = singleActionIcon { Icon(icon) }

internal fun singleActionIcon(id: Int) = singleActionIcon { Icon(vectorResource(id)) }

@FunBinding
suspend fun runRootCommand(
    runShellCommand: runShellCommand,
    toaster: Toaster,
    command: @Assisted String,
) {
    try {
        runShellCommand(command)
    } catch (t: Throwable) {
        t.printStackTrace()
        toaster.toast(R.string.es_no_root)
    }
}

@FunBinding
fun sendIntent(
    applicationContext: ApplicationContext,
    toaster: Toaster,
    intent: @Assisted Intent,
) {
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        PendingIntent.getActivity(
            applicationContext, 99, intent, 0, null
        ).send()
    } catch (t: Throwable) {
        t.printStackTrace()
        toaster.toast(R.string.es_activity_not_found)
    }
}

@FunBinding
suspend fun performGlobalAction(
    services: AccessibilityServices,
    action: @Assisted Int,
) {
    services.performGlobalAction(action)
}
