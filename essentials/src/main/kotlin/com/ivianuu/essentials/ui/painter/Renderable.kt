package com.ivianuu.essentials.ui.painter

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.ui.core.Alignment
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.BlendMode
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.graphics.ScaleFit
import androidx.ui.graphics.Shape
import androidx.ui.graphics.vector.DefaultTintBlendMode
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.graphics.vector.Group
import androidx.ui.graphics.vector.Path
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.VectorGroup
import androidx.ui.graphics.vector.VectorPath
import androidx.ui.graphics.vector.VectorScope
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutSize
import androidx.ui.unit.Size
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.material.Surface

@Stable
interface Renderable {
    @Composable
    fun content()
}

@Composable
fun DrawRenderable(renderable: Renderable) {
    renderable.content()
}

@Stable
class ColorRenderable(
    private val color: Color,
    private val shape: Shape = RectangleShape,
    private val size: Size? = null
) : Renderable {
    @Composable
    override fun content() {
        Surface(
            modifier = size?.let { LayoutSize(it.width, it.height) } ?: Modifier.None,
            color = color,
            shape = shape
        ) {
        }
    }
}

val AvatarSize = Size(40.dp, 40.dp)

@Stable
class ImageRenderable(
    private val image: Image,
    private val tintColor: Color? = null,
    private val size: Size? = null
) : Renderable {
    @Composable
    override fun content() {
        val finalSize = size ?: with(DensityAmbient.current) {
            Size(
                width = image.width.ipx.toDp(),
                height = image.height.ipx.toDp()
            )
        }

        Container(
            width = finalSize.width,
            height = finalSize.height
        ) {
            // todo DrawImage(image = image, tint = tintColor)
        }
    }
}

@Stable
class VectorRenderable(
    private val image: VectorAsset,
    private val tintColor: Color? = null,
    private val tintBlendMode: BlendMode = DefaultTintBlendMode,
    private val size: Size = Size(image.defaultWidth, image.defaultHeight)
) : Renderable {
    @Composable
    override fun content() {
        Container(
            width = size.width,
            height = size.height
        ) {
            DrawVector(
                name = image.name,
                viewportWidth = image.viewportWidth,
                viewportHeight = image.viewportHeight,
                defaultWidth = size.width,
                defaultHeight = size.height,
                tintColor = tintColor ?: contentColor(),
                tintBlendMode = tintBlendMode,
                alignment = Alignment.Center,
                scaleFit = ScaleFit.Fit
            ) { _, _ ->
                RenderVectorGroup(group = image.root)
            }
        }
    }
}

@Composable
private fun VectorScope.RenderVectorGroup(group: VectorGroup) {
    for (vectorNode in group) {
        if (vectorNode is VectorPath) {
            Path(
                pathData = vectorNode.pathData,
                name = vectorNode.name,
                fill = vectorNode.fill,
                fillAlpha = vectorNode.fillAlpha,
                stroke = vectorNode.stroke,
                strokeAlpha = vectorNode.strokeAlpha,
                strokeLineWidth = vectorNode.strokeLineWidth,
                strokeLineCap = vectorNode.strokeLineCap,
                strokeLineJoin = vectorNode.strokeLineJoin,
                strokeLineMiter = vectorNode.strokeLineMiter
            )
        } else if (vectorNode is VectorGroup) {
            Group(
                name = vectorNode.name,
                rotation = vectorNode.rotation,
                scaleX = vectorNode.scaleX,
                scaleY = vectorNode.scaleY,
                translationX = vectorNode.translationX,
                translationY = vectorNode.translationY,
                pivotX = vectorNode.pivotX,
                pivotY = vectorNode.pivotY,
                clipPathData = vectorNode.clipPathData
            ) {
                RenderVectorGroup(group = vectorNode)
            }
        }
    }
}