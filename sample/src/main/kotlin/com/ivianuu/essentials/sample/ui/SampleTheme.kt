/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.rubik.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.injekt.*

@Provide val sampleTheme = AppTheme { content ->
  EsTheme(
    typography = EsTypography.editEach { copy(fontFamily = Rubik) },
    content = content
  )
}
