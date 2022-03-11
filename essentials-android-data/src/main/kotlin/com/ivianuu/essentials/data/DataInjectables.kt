/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import java.io.*

@Tag annotation class DataDirTag {
  companion object {
    @Provide fun dataDir(context: AppContext): DataDir =
      File(context.applicationInfo.dataDir)
  }
}

typealias DataDir = @DataDirTag File

@Tag annotation class PrefsDirTag {
  companion object {
    @Provide fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")
  }
}

typealias PrefsDir = @PrefsDirTag File

@Provide fun packageManager(context: AppContext) = context.packageManager!!
