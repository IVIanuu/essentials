/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import androidx.compose.runtime.compositionLocalOf
import com.ivianuu.injekt.common.Elements

object UiScope

val LocalUiElements = compositionLocalOf<Elements<UiScope>> { error("No ui elements provided") }
