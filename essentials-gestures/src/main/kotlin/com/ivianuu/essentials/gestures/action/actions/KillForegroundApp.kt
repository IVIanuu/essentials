package com.ivianuu.essentials.gestures.action.actions

// todo

/**
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.data.Flag

private fun createKillForegroundAppAction() = Action(
    key = KEY_KILL_FOREGROUND_APP,
    title = string(R.string.action_kill_foreground_app),
    states = stateless(R.drawable.es_ic_clear),
    flags = setOf(Flag.RequiresRoot, Flag.RequiresAccessibilityPermission)
)

@SuppressLint("CheckResult")
private suspend fun killForegroundApp() {
    val currentApp = recentAppsProvider.currentApp.first()

    if (currentApp != "android" &&
        currentApp != "com.android.systemui" &&
        currentApp != context.packageName && // we have no suicidal intentions :D
        currentApp != getHomePackage()
    ) {
        runRootCommand("am force-stop $currentApp")
    }
}

private fun getHomePackage(): String {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }

    return context.packageManager.resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )?.activityInfo?.packageName ?: ""
}*/