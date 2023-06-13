/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.time

import com.ivianuu.injekt.Provide

@Provide actual val DefaultClock = Clock { System.currentTimeMillis().milliseconds }
