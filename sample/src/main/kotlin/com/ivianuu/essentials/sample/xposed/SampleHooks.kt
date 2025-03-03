/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import android.media.AudioTrack
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.xposed.*
import injekt.*

@Provide fun sampleHooks(logger: Logger) = Hooks { config ->
  logger.d { "hello ${config.packageName}" }
  if (config.packageName != "com.spotify.music") return@Hooks

  logger.d { "spot spot spot" }

  hookAllMethods(AudioTrack::class, "write") {
    before {
      logger.d { "write ${args.contentDeepToString()}" }
    }
  }
}
