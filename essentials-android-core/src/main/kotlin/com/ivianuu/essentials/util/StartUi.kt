package com.ivianuu.essentials.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.get

@Reader
suspend fun startUi() {
    val intent = get<PackageManager>().getLaunchIntentForPackage(get<BuildInfo>().packageName)!!
    get<@ForApplication Context>().startActivity(
        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
