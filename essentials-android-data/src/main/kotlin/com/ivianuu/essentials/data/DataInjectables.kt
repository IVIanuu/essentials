/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import java.io.File

@Tag annotation class DataDirTag
typealias DataDir = @DataDirTag File

@Provide fun dataDir(context: AppContext): DataDir =
  File(context.applicationInfo.dataDir)

@Tag annotation class PrefsDirTag
typealias PrefsDir = @PrefsDirTag File

@Provide fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")

@Provide inline fun packageManager(context: AppContext) = context.packageManager!!
