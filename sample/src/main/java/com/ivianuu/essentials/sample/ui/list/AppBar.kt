package com.ivianuu.essentials.sample.ui.list

import androidx.compose.ViewComposition
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.currentTextStyle
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.core.Surface
import com.ivianuu.essentials.ui.compose.material.rippleBackground
import com.ivianuu.essentials.ui.compose.view.ConstraintLayout
import com.ivianuu.essentials.ui.compose.view.FrameLayout
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.ImageView
import com.ivianuu.essentials.ui.compose.view.MatchParent
import com.ivianuu.essentials.ui.compose.view.ParentId
import com.ivianuu.essentials.ui.compose.view.WrapContent
import com.ivianuu.essentials.ui.compose.view.backgroundColor
import com.ivianuu.essentials.ui.compose.view.centerVerticalIn
import com.ivianuu.essentials.ui.compose.view.constraintLeftToLeftOf
import com.ivianuu.essentials.ui.compose.view.constraintLeftToRightOf
import com.ivianuu.essentials.ui.compose.view.constraintRightToLeftOf
import com.ivianuu.essentials.ui.compose.view.constraintRightToRightOf
import com.ivianuu.essentials.ui.compose.view.elevation
import com.ivianuu.essentials.ui.compose.view.height
import com.ivianuu.essentials.ui.compose.view.id
import com.ivianuu.essentials.ui.compose.view.image
import com.ivianuu.essentials.ui.compose.view.imageColor
import com.ivianuu.essentials.ui.compose.view.margin
import com.ivianuu.essentials.ui.compose.view.onClick
import com.ivianuu.essentials.ui.compose.view.padding
import com.ivianuu.essentials.ui.compose.view.size
import com.ivianuu.essentials.ui.compose.view.width
import com.ivianuu.essentials.ui.compose.view.wrapContent
import com.ivianuu.essentials.ui.compose.viewId

private val leadingId = viewId()
private val titleId = viewId()
private val trailingId = viewId()

fun ViewComposition.AppBar(
    color: Color = +themeColor { primary },
    leading: (ViewComposition.() -> Unit)? = null,
    title: (ViewComposition.() -> Unit)? = null,
    trailing: (ViewComposition.() -> Unit)? = null
) {
    ConstraintLayout {
        width(MatchParent)
        height(56.dp)
        backgroundColor(color)
        elevation(4.dp)

        Surface(color) {
            if (leading != null) {
                FrameLayout {
                    id(leadingId)
                    wrapContent()
                    constraintLeftToLeftOf(ParentId)
                    centerVerticalIn(ParentId)
                    margin(left = 16.dp)

                    leading()
                }
            }

            if (title != null) {
                FrameLayout {
                    id(titleId)
                    width(MatchParent)
                    height(WrapContent)
                    if (leading != null) {
                        constraintLeftToRightOf(leadingId)
                    } else {
                        constraintLeftToLeftOf(ParentId)
                    }
                    if (trailing != null) {
                        constraintRightToLeftOf(trailingId)
                    } else {
                        constraintRightToRightOf(ParentId)
                    }
                    centerVerticalIn(ParentId)
                    margin(left = 16.dp, right = 16.dp)

                    CurrentTextStyleProvider(value = +themeTextStyle { h6 }) {
                        title()
                    }
                }
            }

            if (trailing != null) {
                FrameLayout {
                    id(trailingId)
                    wrapContent()
                    constraintRightToRightOf(ParentId)
                    centerVerticalIn(ParentId)
                    margin(right = 16.dp)

                    trailing()
                }
            }
        }
    }
}

fun ViewComposition.AppBarIcon(
    image: Image,
    color: Color? = (+currentTextStyle()).color,
    onClick: () -> Unit
) {
    ImageView {
        size(32.dp)
        padding(4.dp)
        image(image)
        rippleBackground(false)
        color?.let { imageColor(it) }
        onClick(onClick)
    }
}