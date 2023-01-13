/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.hardware

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun consumerIrManager(context: Context): ConsumerIrManager =
    context.getSystemService(ConsumerIrManager::class.java)

  @Provide inline fun sensorManager(context: Context): SensorManager =
    context.getSystemService(SensorManager::class.java)
}
