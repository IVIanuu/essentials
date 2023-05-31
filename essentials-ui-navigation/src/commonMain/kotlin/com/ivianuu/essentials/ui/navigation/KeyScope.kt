/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import com.ivianuu.injekt.common.Elements

object KeyScope

val LocalKeyElements = compositionLocalOf<Elements<KeyScope>> { error("No key elements provided") }
