package com.ivianuu.essentials.sample.ui.list

import androidx.compose.ViewComposition
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.core.withContext
import com.ivianuu.essentials.ui.compose.view.*
import com.ivianuu.kommon.core.content.drawableAttr

private val leadingId = viewId()
private val titleId = viewId()
private val trailingId = viewId()

fun ViewComposition.AppBar(
    leading: (ViewComposition.() -> Unit)? = null,
    title: (ViewComposition.() -> Unit)? = null,
    trailing: (ViewComposition.() -> Unit)? = null
) {
    RelativeLayout {
        width(Dp.MATCH_PARENT)
        height(56.dp)
        background(color = Color.Blue)
        elevation(4.dp)

        if (leading != null) {
            FrameLayout {
                id(leadingId)
                wrapContent()
                alignParentLeft()
                centerVertical()
                margin(left = 16.dp)

                leading()
            }
        }

        // todo default text style
        if (title != null) {
            FrameLayout {
                id(titleId)
                height(Dp.WRAP_CONTENT)
                width(Dp.MATCH_PARENT)
                margin(left = 16.dp, right = 16.dp)
                if (leading != null) {
                    toRightOf(leadingId)
                } else {
                    alignParentLeft()
                }
                if (trailing != null) {
                    toLeftOf(trailingId)
                } else {
                    alignParentRight()
                }
                centerVertical()

                title()
            }
        }

        if (trailing != null) {
            FrameLayout {
                id(trailingId)
                wrapContent()
                alignParentRight()
                centerVertical()
                margin(right = 16.dp)

                trailing()
            }
        }
    }
}

fun ViewComposition.AppBarIcon(
        image: Image,
        color: Color? = null,
        onClick: () -> Unit
) {
    ImageView {
        size(32.dp)
        padding(4.dp)
        image(image)
        color?.let { imageColor(it) }
        +withContext {
            background(drawable = drawableAttr(R.attr.selectableItemBackgroundBorderless))
        }
        onClick(onClick)
    }
}