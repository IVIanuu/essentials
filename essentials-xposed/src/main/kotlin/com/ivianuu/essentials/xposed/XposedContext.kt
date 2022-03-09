/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import android.content.pm.*
import de.robv.android.xposed.callbacks.*

interface XposedContext {
  @Provide val packageName: PackageName

  @Provide val processName: ProcessName

  @Provide val classLoader: ClassLoader

  @Provide val appInfo: ApplicationInfo
}

@JvmInline value class ModulePackageName(val value: String)

@JvmInline value class PackageName(val value: String)

@JvmInline value class ProcessName(val value: String)

class XposedContextImpl(
  private val params: XC_LoadPackage.LoadPackageParam
) : XposedContext {
  @Provide override val packageName: PackageName
    get() = PackageName(params.packageName)

  @Provide override val processName: ProcessName
    get() = ProcessName(params.processName)

  @Provide override val classLoader: ClassLoader
    get() = params.classLoader

  @Provide override val appInfo: ApplicationInfo
    get() = params.appInfo
}
