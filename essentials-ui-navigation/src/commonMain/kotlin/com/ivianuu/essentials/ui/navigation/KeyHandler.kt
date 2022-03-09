/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation


fun interface KeyHandler<R> : suspend (Key<R>, (R) -> Unit) -> Boolean

@Provide fun defaultKeyHandlers() = emptyList<KeyHandler<*>>()
