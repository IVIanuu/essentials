
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import injekt.*
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

@Tag typealias Now = Duration
@Provide fun now(): Now = System.currentTimeMillis().milliseconds
