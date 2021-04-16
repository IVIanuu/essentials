package com.ivianuu.essentials.util

import com.ivianuu.injekt.*
import kotlin.time.*

@Given
actual val timestampProvider: TimestampProvider = { System.currentTimeMillis().milliseconds }
