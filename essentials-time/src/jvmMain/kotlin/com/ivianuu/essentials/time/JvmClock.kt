/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.time

internal actual val clock = Clock { System.nanoTime().nanoseconds }
