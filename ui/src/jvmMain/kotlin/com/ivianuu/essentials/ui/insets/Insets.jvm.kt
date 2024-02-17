/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.insets

import com.ivianuu.injekt.*

@Provide actual val windowInsetsProvider = WindowInsetsProvider { it() }
