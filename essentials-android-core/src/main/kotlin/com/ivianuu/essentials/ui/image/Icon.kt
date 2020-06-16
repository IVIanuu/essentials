package com.ivianuu.essentials.ui.image

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
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
import androidx.ui.layout.size
import androidx.ui.res.imageResource
import androidx.ui.unit.dp

@Composable
fun Icon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = contentColor(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    Image(
        painter = painter,
        modifier = Modifier.size(24.dp).plus(modifier),
        colorFilter = ColorFilter(tint, BlendMode.srcIn),
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha
    )
}

@Composable
fun Icon(
    id: Int,
    modifier: Modifier = Modifier,
    tint: Color = contentColor(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    Icon(
        icon = imageResource(id),
        modifier = modifier,
        tint = tint,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha
    )
}

@Composable
fun Icon(
    icon: ImageAsset,
    modifier: Modifier = Modifier,
    tint: Color = contentColor(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    Icon(
        painter = remember(icon) { ImagePainter(icon) },
        modifier = modifier,
        tint = tint,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha
    )
}

@Composable
fun Icon(
    icon: VectorAsset,
    modifier: Modifier = Modifier,
    tint: Color = contentColor(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    Icon(
        painter = VectorPainter(icon),
        modifier = modifier,
        tint = tint,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha
    )
}
