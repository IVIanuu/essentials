/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.data

import essentials.*
import injekt.*
import java.io.*

@Tag typealias CacheDir = File

@Tag typealias DataDir = File

@Tag typealias PrefsDir = File

@Provide object DirProviders {
  @Provide fun cacheDir(dataDir: DataDir): CacheDir = dataDir.resolve("cache")

  @Provide fun dataDir(appContext: AppContext): DataDir = File(appContext.applicationInfo.dataDir)

  @Provide fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")
}
