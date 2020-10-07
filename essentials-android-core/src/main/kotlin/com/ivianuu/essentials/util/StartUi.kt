package com.ivianuu.essentials.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.platform.ContextAmbient
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.CompletableDeferred

@FunBinding
suspend fun startUi(
    applicationContext: ApplicationContext,
    buildInfo: BuildInfo,
    navigator: Navigator,
    packageManager: PackageManager,
): Activity {
    val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
    val deferredActivity = CompletableDeferred<Activity>()

    navigator.push(
        Route(opaque = true) {
            if (!deferredActivity.isCompleted) {
                navigator.popTop()
                deferredActivity.complete(ContextAmbient.current as Activity)
            }
        }
    )

    applicationContext.startActivity(
        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )

    return deferredActivity.await()
}
