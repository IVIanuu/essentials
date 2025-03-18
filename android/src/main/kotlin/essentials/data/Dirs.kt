/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.data

import android.content.*
import essentials.*
import injekt.*
import java.io.*

data class AppDirs(val cache: File, val data: File, val prefs: File) {
  @Provide companion object {
    @Provide fun default(context: Context): @Scoped<AppScope> AppDirs {
      val dataDir = File(context.applicationInfo.dataDir)
      return AppDirs(
        cache = dataDir.resolve("cache"),
        data = dataDir,
        prefs = dataDir.resolve("shared_prefs")
      )
    }
  }
}
