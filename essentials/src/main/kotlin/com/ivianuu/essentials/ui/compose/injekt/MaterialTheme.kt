package com.ivianuu.essentials.ui.compose.injekt

import androidx.compose.Effect
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTypography
import com.ivianuu.essentials.ui.compose.material.resourceMaterialColors
import com.ivianuu.essentials.ui.compose.material.resourceMaterialTypography
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module

val composeModule = module {
    factory {
        MaterialThemeProvider(
            colors = resourceMaterialColors(),
            typography = resourceMaterialTypography()
        )
    }
}

data class MaterialThemeProvider(
    val colors: Effect<MaterialColors>,
    val typography: Effect<MaterialTypography>
)