package com.ivianuu.essentials.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.platform.ContextAmbient
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.CompletableDeferred

@Reader
suspend fun startUi(): Activity {
    val intent = given<PackageManager>().getLaunchIntentForPackage(given<BuildInfo>().packageName)!!
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
