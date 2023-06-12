/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.runtime.compositionLocalOf
import com.ivianuu.essentials.Scope

object UiScope

val LocalScope = compositionLocalOf<Scope<*>> { error("No scope provided") }
