/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.rate.RateUiSchedule
import com.ivianuu.injekt.Provide
import kotlin.time.Duration.Companion.minutes

@Provide val sampleRateUiSchedule: RateUiSchedule
  get() = RateUiSchedule(
    minInstallDuration = 1.minutes,
    minLaunchTimes = 5
  )
