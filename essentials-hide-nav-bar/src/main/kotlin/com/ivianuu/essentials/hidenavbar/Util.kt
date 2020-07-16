package com.ivianuu.essentials.hidenavbar

import android.graphics.Rect
import android.os.IBinder
import android.view.Display
import com.ivianuu.essentials.datastore.android.settings.SettingDataStore
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.datastore.android.settings.int
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@Reader
internal suspend fun disableNonSdkInterfaceDetection() {
    val systemBuildInfo = given<SystemBuildInfo>()
    val settingsDataStoreFactory = given<SettingsDataStoreFactory>()
    if (systemBuildInfo.sdk >= 29) {
        d { "disable non sdk on 29" }

        val hiddenApiPolicy = given<SettingsDataStoreFactory>().int(
            "hidden_api_policy", SettingDataStore.Type.Global
        )
        hiddenApiPolicy.updateData { 1 }
    } else if (systemBuildInfo.sdk >= 28) {
        d { "disable non sdk on p" }

        val hiddenApiPrePieAppsSetting = settingsDataStoreFactory.int(
            "hidden_api_policy_pre_p_apps",
            SettingDataStore.Type.Global
        )
        val hiddenApiOnPieAppsSetting = settingsDataStoreFactory.int(
            "hidden_api_policy_p_apps",
            SettingDataStore.Type.Global
        )

        hiddenApiPrePieAppsSetting.updateData { 1 }
        hiddenApiOnPieAppsSetting.updateData { 1 }
    }
}

@Reader
internal fun setOverscan(rect: Rect) {
    d { "set overscan $rect" }

    val cls = Class.forName("android.view.IWindowManager\$Stub")
    val invoke = Class.forName("android.os.ServiceManager")
        .getMethod("checkService", String::class.java)
        .invoke(null, "window")

    val windowManagerService = cls.getMethod("asInterface", IBinder::class.java)
        .invoke(null, invoke)!!

    val setOverscanMethod = windowManagerService.javaClass.getDeclaredMethod(
        "setOverscan",
        Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java
    ).apply { isAccessible = true }

    setOverscanMethod.invoke(
        windowManagerService,
        Display.DEFAULT_DISPLAY, rect.left, rect.top, rect.right, rect.bottom
    )
}
