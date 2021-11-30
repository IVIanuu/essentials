/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.time

import com.ivianuu.injekt.Provide

@Provide actual val clock = Clock { System.nanoTime().nanoseconds }
