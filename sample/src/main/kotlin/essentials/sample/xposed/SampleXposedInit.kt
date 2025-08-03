/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.xposed

import essentials.xposed.*
import injekt.*

class SampleXposedInit : EsXposedInit({
  @Provide val modulePackageName = ModulePackageName("essentials.sample")
  create()
})
