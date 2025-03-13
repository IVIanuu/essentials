
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.time

import injekt.*
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

@Tag typealias Now = Duration
typealias Clock = () -> Now
@Provide fun currentTime(): Now = System.currentTimeMillis().milliseconds
