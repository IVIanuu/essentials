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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.getOrElse
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.twilight.domain.TwilightState
import com.ivianuu.essentials.twilight.ui.TwilightTheme
import com.ivianuu.essentials.ui.AppTheme
import com.ivianuu.essentials.ui.animation.transition.HorizontalStackTransition
import com.ivianuu.essentials.ui.animation.transition.LocalStackTransition
import com.ivianuu.essentials.ui.material.editEach
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.StateFlow

@Provide fun sampleTheme(
  rp: ResourceProvider,
  twilightState: StateFlow<TwilightState>
): AppTheme = { content ->
  val robotoMono = remember {
    FontFamily(
      catch { loadResource<Typeface>(R.font.roboto_mono) }
        .getOrElse { Typeface(android.graphics.Typeface.DEFAULT) }
    )
  }
  TwilightTheme(
    shapes = Shapes(
      medium = RoundedCornerShape(12.dp)
    ),
    typography = Typography().editEach { copy(fontFamily = robotoMono) },
    twilightState = twilightState.collectAsState().value
  ) {
    CompositionLocalProvider(
      LocalStackTransition provides HorizontalStackTransition(),
      content = content
    )
  }
}
