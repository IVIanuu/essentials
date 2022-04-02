/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.boot

import com.ivianuu.injekt.Provide

fun interface BootListener : () -> Unit

@Provide val defaultBootListeners: Collection<BootListener> get() = emptyList()
