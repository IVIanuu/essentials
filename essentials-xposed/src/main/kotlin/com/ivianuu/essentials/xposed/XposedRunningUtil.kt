/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed


@JvmInline value class IsXposedRunning(val value: Boolean)

@Provide val isXposedRunning: IsXposedRunning get() = IsXposedRunning(false)
