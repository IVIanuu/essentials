/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.essentials.time.*

@Provide val sampleRateUiSchedule = RateUiSchedule(
  minInstallDuration = 1.minutes,
  minLaunchTimes = 5
)
