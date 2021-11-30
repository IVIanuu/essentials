/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.rubik.Rubik
import com.ivianuu.essentials.ui.AppTheme
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.material.EsTheme
import com.ivianuu.essentials.ui.material.EsTypography
import com.ivianuu.essentials.ui.material.editEach
import com.ivianuu.injekt.Provide

@Provide val sampleTheme = AppTheme { content ->
  EsTheme(
    typography = EsTypography.editEach { copy(fontFamily = Rubik) },
    content = content
  )
}
