package com.ivianuu.essentials.gestures.action.actions

import android.app.PendingIntent
import android.content.Intent
import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.coil.CoilImage
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.flowOf

internal fun coilActionIcon(data: Any): ActionIcon = flowOf { CoilImage(data = data) }

internal fun singleActionIcon(icon: @Composable () -> Unit): ActionIcon = flowOf(icon)

internal fun singleActionIcon(icon: VectorAsset) = singleActionIcon { Icon(icon) }

internal fun singleActionIcon(id: Int) = singleActionIcon { Icon(vectorResource(id)) }

@Reader
internal suspend fun runRootCommand(command: String) {
    try {
        Shell.run(command)
    } catch (e: Exception) {
        e.printStackTrace()
        Toaster.toast(R.string.es_no_root)
    }
}

@Reader
internal fun Intent.send() {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        PendingIntent.getActivity(
            applicationContext, 99, this, 0, null
        ).send()
    } catch (e: Exception) {
        e.printStackTrace()
        Toaster.toast(R.string.es_activity_not_found)
    }
}

@Reader
internal suspend fun performGlobalAction(action: Int) {
    given<AccessibilityServices>().performGlobalAction(action)
}
