/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.xposed

import essentials.*
import essentials.xposed.*
import injekt.*

@Provide fun sampleHooks(logger: Logger = inject) = Hooks { config ->
  d { "hello ${config.packageName}" }
  if (config.packageName != "com.offtrack.app") return@Hooks

  d { "spot spot spot" }

  hookAllConstructors("d6.i") {
    before {
      d { "TrackSegment ${args.contentDeepToString()}" }
    }
  }
}
