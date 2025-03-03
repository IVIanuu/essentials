/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample

import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.logging.*
import injekt.*

@Provide fun sampleFirstRunHandler(logger: Logger) = FirstRunHandler {
  logger.d { "hello" }
}
