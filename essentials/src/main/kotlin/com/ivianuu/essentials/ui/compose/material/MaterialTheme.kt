/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.text.TextStyle
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.util.colorAttr

@Composable
fun ResourceMaterialTheme(
    colors: ColorPalette = resourceColors(),
    typography: Typography = resourceTypography(),
    children: @Composable() () -> Unit
) = composable("ResourceMaterialTheme") {
    MaterialTheme(
        colors = colors,
        typography = typography
    ) {
        children()
    }
}

fun resourceColors(): ColorPalette = effect {
    val context = ambient(ContextAmbient)
    memo {
        ColorPalette(
            primary = Color(context.colorAttr(R.attr.colorPrimary)),
            primaryVariant = Color(context.colorAttr(R.attr.colorPrimaryVariant)),
            secondary = Color(context.colorAttr(R.attr.colorSecondary)),
            secondaryVariant = Color(context.colorAttr(R.attr.colorSecondaryVariant)),
            background = Color(context.colorAttr(android.R.attr.colorBackground)),
            surface = Color(context.colorAttr(R.attr.colorSurface)),
            error = Color(context.colorAttr(R.attr.colorError)),
            onPrimary = Color(context.colorAttr(R.attr.colorOnPrimary)),
            onSecondary = Color(context.colorAttr(R.attr.colorOnSecondary)),
            onBackground = Color(context.colorAttr(R.attr.colorOnBackground)),
            onSurface = Color(context.colorAttr(R.attr.colorOnSurface)),
            onError = Color(context.colorAttr(R.attr.colorOnError))
        )
    }
}

fun resourceTypography(): Typography = effect {
    memo { Typography() }
    /*Typography(
        h1 = +resourceTextStyle(R.attr.textAppearanceHeadline1),
        h2 = +resourceTextStyle(R.attr.textAppearanceHeadline2),
        h3 = +resourceTextStyle(R.attr.textAppearanceHeadline3),
        h4 = +resourceTextStyle(R.attr.textAppearanceHeadline4),
        h5 = +resourceTextStyle(R.attr.textAppearanceHeadline5),
        h6 = +resourceTextStyle(R.attr.textAppearanceHeadline6),
        subtitle1 = +resourceTextStyle(R.attr.textAppearanceSubtitle1),
        subtitle2 = +resourceTextStyle(R.attr.textAppearanceSubtitle2),
        body1 = +resourceTextStyle(R.attr.textAppearanceBody1),
        body2 = +resourceTextStyle(R.attr.textAppearanceBody2),
        button = +resourceTextStyle(R.attr.textAppearanceButton),
        caption = +resourceTextStyle(R.attr.textAppearanceCaption),
        overline = +resourceTextStyle(R.attr.textAppearanceOverline)
    )*/
}

fun resourceTextStyle(attr: Int): TextStyle = effect {
    error("not implemented")
    val context = ambient(ContextAmbient)
    TextStyle()
}

inline fun Typography.editEach(edit: TextStyle.() -> TextStyle): Typography {
    return Typography(
        h1 = edit(h1),
        h2 = edit(h2),
        h3 = edit(h3),
        h4 = edit(h4),
        h5 = edit(h5),
        h6 = edit(h6),
        subtitle1 = edit(subtitle1),
        subtitle2 = edit(subtitle2),
        body1 = edit(body1),
        body2 = edit(body2),
        button = edit(button),
        caption = edit(caption),
        overline = edit(overline)
    )
}

fun ColorPalette.copy(
    primary: Color = this.primary,
    primaryVariant: Color = this.primaryVariant,
    secondary: Color = this.secondary,
    secondaryVariant: Color = this.secondaryVariant,
    background: Color = this.background,
    surface: Color = this.surface,
    error: Color = this.error,
    onPrimary: Color = this.onPrimary,
    onSecondary: Color = this.onSecondary,
    onBackground: Color = this.onBackground,
    onSurface: Color = this.onSurface,
    onError: Color = this.onError
) = ColorPalette(
    primary = primary,
    primaryVariant = primaryVariant,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError
)