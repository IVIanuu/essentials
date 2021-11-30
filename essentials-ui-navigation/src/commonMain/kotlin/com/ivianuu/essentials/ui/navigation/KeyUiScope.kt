/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import com.ivianuu.injekt.common.Elements

object KeyUiScope

val LocalKeyUiElements = compositionLocalOf<Elements<KeyUiScope>> { error("No key ui elements provided") }