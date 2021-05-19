package com.ivianuu.essentials.sample

import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.injekt.*
import kotlin.time.*

@Provide val sampleRateUiSchedule = RateUiSchedule(
  minInstallDuration = 1.minutes,
  minLaunchTimes = 5
)
