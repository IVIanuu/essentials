package com.ivianuu.essentials.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@Reader
suspend fun startUi() {
    val intent = given<PackageManager>().getLaunchIntentForPackage(given<BuildInfo>().packageName)!!
    applicationContext.startActivity(
        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
