package com.ivianuu.essentials.ui.image

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.BlendMode
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.DefaultAlpha
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.ScaleFit
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.graphics.painter.Painter
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.VectorPainter
import androidx.ui.layout.LayoutSize
import androidx.ui.res.imageResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrNull

@Immutable
data class IconStyle(
    val modifier: Modifier,
    val alignment: Alignment,
    val scaleFit: ScaleFit,
    val alpha: Float,
    val tint: Color
)

fun IconStyle.toImageStyle() = ImageStyle(
    modifier, alignment, scaleFit, alpha, ColorFilter(tint, BlendMode.srcIn)
)

val IconStyleAmbient = staticAmbientOf<IconStyle>()

@Composable
fun DefaultIconStyle(
    tint: Color = contentColor(),
    modifier: Modifier = LayoutSize(24.dp),
    alignment: Alignment = Alignment.Center,
    scaleFit: ScaleFit = ScaleFit.Fit,
    alpha: Float = DefaultAlpha
) = IconStyle(
    modifier = modifier,
    alignment = alignment,
    scaleFit = scaleFit,
    alpha = alpha,
    tint = tint
)

@Composable
fun Icon(
    painter: Painter,
    modifier: Modifier = Modifier.None,
    style: IconStyle = IconStyleAmbient.currentOrNull ?: DefaultIconStyle()
) {
    Image(
        painter = painter,
        modifier = modifier,
        style = remember(style) { style.toImageStyle() }
    )
}

@Composable
fun Icon(
    id: Int,
    modifier: Modifier = Modifier.None,
    style: IconStyle = IconStyleAmbient.currentOrNull ?: DefaultIconStyle()
) {
    Icon(
        icon = imageResource(id),
        modifier = modifier,
        style = style
    )
}

@Composable
fun Icon(
    icon: ImageAsset,
    modifier: Modifier = Modifier.None,
    style: IconStyle = IconStyleAmbient.currentOrNull ?: DefaultIconStyle()
) {
    Icon(
        painter = remember(icon) { ImagePainter(icon) },
        modifier = modifier,
        style = style
    )
}

@Composable
fun Icon(
    icon: VectorAsset,
    modifier: Modifier = Modifier.None,
    style: IconStyle = IconStyleAmbient.currentOrNull ?: DefaultIconStyle()
) {
    Icon(
        painter = VectorPainter(icon),
        modifier = modifier,
        style = style
    )
}
