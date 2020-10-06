package com.ivianuu.essentials.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.MergeInto

@MergeInto(ApplicationComponent::class)
@Module
object AndroidUtilModule {

    @Binding
    fun systemBuildInfo() = SystemBuildInfo(Build.VERSION.SDK_INT)

    @Binding(ApplicationComponent::class)
    fun buildInfo(
        applicationContext: ApplicationContext,
        packageManager: PackageManager,
    ): BuildInfo {
        val appInfo = applicationContext.applicationInfo
        val packageInfo = packageManager
            .getPackageInfo(appInfo.packageName, 0)
        return BuildInfo(
            isDebug = appInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE),
            packageName = appInfo.packageName,
            versionCode = packageInfo.versionCode
        )
    }

    @Binding
    fun deviceInfo() = DeviceInfo(model = Build.MODEL, manufacturer = Build.MANUFACTURER)

    @Binding(ApplicationComponent::class)
    fun androidLogger(
        buildInfo: BuildInfo,
        androidLoggerProvider: () -> AndroidLogger,
    ): Logger? {
        return if (buildInfo.isDebug) androidLoggerProvider() else null
    }

    @Binding
    fun packageManager(applicationContext: ApplicationContext) = applicationContext.packageManager!!

}