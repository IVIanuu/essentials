/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.boot

import com.ivianuu.injekt.*

fun interface BootListener : () -> Unit

@Provide val defaultBootListeners: Collection<BootListener> get() = emptyList()
