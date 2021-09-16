package com.ivianuu.essentials.robotomono

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import com.github.michaelbull.result.getOrElse
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.material.editEach
import com.ivianuu.injekt.Inject

@Composable fun robotoMonoTypography(
  @Inject rp: ResourceProvider
): Typography {
  val robotoMono = remember {
    FontFamily(
      catch { loadResource<Typeface>(R.font.roboto_mono) }
        .getOrElse { Typeface(android.graphics.Typeface.DEFAULT) }
    )
  }

  return remember { Typography().editEach { copy(fontFamily = robotoMono) } }
}
