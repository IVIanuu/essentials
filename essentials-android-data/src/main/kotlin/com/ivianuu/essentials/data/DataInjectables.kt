/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import java.io.*

@Tag annotation class DataDirTag
typealias DataDir = @DataDirTag File

@Provide fun dataDir(context: AppContext): DataDir =
  File(context.applicationInfo.dataDir)

@Tag annotation class PrefsDirTag
typealias PrefsDir = @PrefsDirTag File

@Provide fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")

@Provide inline fun packageManager(context: AppContext) = context.packageManager!!
