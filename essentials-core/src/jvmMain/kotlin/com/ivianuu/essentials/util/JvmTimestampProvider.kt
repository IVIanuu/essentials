package com.ivianuu.essentials.util

import com.ivianuu.injekt.Given
import kotlin.time.milliseconds

@Given
actual val timestampProvider: TimestampProvider = { System.currentTimeMillis().milliseconds }
