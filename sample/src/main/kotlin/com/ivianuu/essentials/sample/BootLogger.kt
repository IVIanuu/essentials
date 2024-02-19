/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import co.touchlab.kermit.*
import com.ivianuu.essentials.boot.*
import com.ivianuu.injekt.*

@Provide fun bootLogger(logger: Logger) = BootListener { logger.d { "booted!" } }
