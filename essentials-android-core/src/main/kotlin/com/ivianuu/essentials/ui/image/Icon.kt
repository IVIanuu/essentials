package com.ivianuu.essentials.ui.image

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.BlendMode
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.DefaultAlpha
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.graphics.painter.Painter
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.VectorPainter
import androidx.ui.layout.preferredSize
import androidx.ui.res.imageResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class IconStyle(
    val modifier: Modifier,
    val alignment: Alignment,
    val contentScale: ContentScale,
    val alpha: Float,
    val tint: Color
)

fun IconStyle.toImageStyle() = ImageStyle(
    modifier, alignment, contentScale, alpha, ColorFilter(tint, BlendMode.srcIn)
)

val IconStyleAmbient = staticAmbientOf<IconStyle>()

@Composable
fun DefaultIconStyle(
    tint: Color = contentColor(),
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) = IconStyle(
    modifier = modifier,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    tint = tint
)

@Composable
fun Icon(
    painter: Painter,
    modifier: Modifier = Modifier,
    style: IconStyle = IconStyleAmbient.currentOrElse { DefaultIconStyle() }
) {
    Image(
        painter = painter,
        modifier = Modifier.preferredSize(24.dp).plus(modifier),
        style = remember(style) { style.toImageStyle() }
    )
}

@Composable
fun Icon(
    id: Int,
    modifier: Modifier = Modifier,
    style: IconStyle = IconStyleAmbient.currentOrElse { DefaultIconStyle() }
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
    modifier: Modifier = Modifier,
    style: IconStyle = IconStyleAmbient.currentOrElse { DefaultIconStyle() }
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
    modifier: Modifier = Modifier,
    style: IconStyle = IconStyleAmbient.currentOrElse { DefaultIconStyle() }
) {
    Icon(
        painter = VectorPainter(icon),
        modifier = modifier,
        style = style
    )
}
