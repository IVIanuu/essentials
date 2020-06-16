package com.ivianuu.essentials.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

@Transient
class StartUiUseCase(
    private val application: @ForApplication Context,
    private val buildInfo: BuildInfo,
    private val packageManager: PackageManager
) {

    operator fun invoke() {
        val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
        application.startActivity(
            intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

}
