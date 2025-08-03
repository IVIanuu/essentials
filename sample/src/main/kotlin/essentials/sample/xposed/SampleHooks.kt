/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.xposed

import android.media.*
import essentials.*
import essentials.xposed.*
import injekt.*

@Provide fun sampleHooks(logger: Logger = inject) = Hooks { config ->
  d { "hello ${config.packageName}" }
  if (config.packageName != "com.offtrack.app") return@Hooks

  d { "spot spot spot" }

  hookAllMethods(AudioTrack::class, "write") {
    before {
      d { "write ${args.contentDeepToString()}" }
    }
  }
}
