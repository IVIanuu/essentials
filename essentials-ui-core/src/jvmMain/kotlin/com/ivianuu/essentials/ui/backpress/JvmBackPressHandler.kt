/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.backpress

import com.ivianuu.injekt.*

@Provide actual val backPressHandlerProvider = BackPressHandlerProvider { it() }
