/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.rubik.Rubik
import com.ivianuu.essentials.ui.AppTheme
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.material.EsTheme
import com.ivianuu.essentials.ui.material.EsTypography
import com.ivianuu.essentials.ui.material.editEach
import com.ivianuu.injekt.Provide

@Provide val sampleTheme: UiDecorator<AppTheme> = { content ->
  EsTheme(
    typography = EsTypography.editEach { copy(fontFamily = Rubik) },
    content = content
  )
}
