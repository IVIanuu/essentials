/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.xposed

import essentials.xposed.*
import injekt.*

@Provide fun sampleHooks() = Hooks { config ->
  @Provide val provided = config

  println("hello ${config.packageName}")
  if (config.packageName != "com.offtrack.app") return@Hooks

  println("offtrack spot spot spot")

  hookAllConstructors("q6.i") {
    before {
      println("offtrack TrackSegment ${args.contentDeepToString()}")
    }
  }
}

