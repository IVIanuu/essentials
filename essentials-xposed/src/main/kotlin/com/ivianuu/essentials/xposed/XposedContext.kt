package com.ivianuu.essentials.xposed

import android.content.pm.ApplicationInfo
import com.ivianuu.injekt.Provide
import de.robv.android.xposed.callbacks.XC_LoadPackage

interface XposedContext {
  @Provide val packageName: PackageName

  @Provide val processName: ProcessName

  @Provide val classLoader: ClassLoader

  @Provide val appInfo: ApplicationInfo
}

typealias PackageName = String

typealias ProcessName = String

class XposedContextImpl(private val params: XC_LoadPackage.LoadPackageParam) : XposedContext {
  @Provide override val packageName: PackageName
    get() = params.packageName

  @Provide override val processName: ProcessName
    get() = params.processName

  @Provide override val classLoader: ClassLoader
    get() = params.classLoader

  @Provide override val appInfo: ApplicationInfo
    get() = params.appInfo
}
