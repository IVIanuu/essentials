/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.FirstRunHandler
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.invoke
import com.ivianuu.injekt.Provide

@Provide fun sampleFirstRunHandler(logger: Logger) = FirstRunHandler {
  logger { "hello" }
}
