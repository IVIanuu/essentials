/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import java.io.*

@Tag annotation class DataDirTag {
  @Provide companion object {
    @Provide fun dataDir(appContext: AppContext): DataDir = File(appContext.applicationInfo.dataDir)
  }
}

typealias DataDir = @DataDirTag File

@Tag annotation class CacheDirTag {
  @Provide companion object {
    @Provide fun cacheDir(dataDir: DataDir): CacheDir = dataDir.resolve("cache")
  }
}

typealias CacheDir = @CacheDirTag File

@Tag annotation class PrefsDirTag {
  @Provide companion object {
    @Provide fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")
  }
}

typealias PrefsDir = @PrefsDirTag File
