package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTheme
import androidx.ui.material.MaterialTypography
import androidx.ui.text.TextStyle
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.util.colorAttr

@Composable
fun ResourceMaterialTheme(
    colors: MaterialColors = +resourceMaterialColors(),
    typography: MaterialTypography = +resourceMaterialTypography(),
    children: @Composable() () -> Unit
) = composable("ResourceMaterialTheme") {
    MaterialTheme(
        colors = colors,
        typography = typography
    ) {
        children()
    }
}

fun resourceMaterialColors() = effectOf<MaterialColors> {
    val context = +ambient(ContextAmbient)
    MaterialColors(
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

fun resourceMaterialTypography() = effectOf<MaterialTypography> {
    MaterialTypography()
    /*MaterialTypography(
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

fun resourceTextStyle(attr: Int) = effectOf<TextStyle> {
    error("not implemented")
    val context = +ambient(ContextAmbient)
    TextStyle()
}