package com.ivianuu.essentials.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.LaunchedTask
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@FunBinding
suspend fun startUi(
    applicationContext: ApplicationContext,
    buildInfo: BuildInfo,
    navigator: Navigator,
    packageManager: PackageManager,
): Activity {
    val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
    return suspendCoroutine { continuation ->
        var completed = false
        navigator.push(
            Route(opaque = true) {
                if (!completed) {
                    completed = true
                    navigator.popTop()
                    val activity = compositionActivity
                    LaunchedTask { continuation.resume(activity) }
                }
            }
        )

        applicationContext.startActivity(
            intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
