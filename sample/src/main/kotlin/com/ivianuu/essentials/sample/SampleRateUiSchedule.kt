/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.rate.domain.RateUiSchedule
import com.ivianuu.essentials.time.minutes
import com.ivianuu.injekt.Provide

@Provide val sampleRateUiSchedule = RateUiSchedule(
  minInstallDuration = 1.minutes,
  minLaunchTimes = 5
)
