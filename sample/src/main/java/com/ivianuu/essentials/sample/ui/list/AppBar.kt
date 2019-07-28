package com.ivianuu.essentials.sample.ui.list

import androidx.compose.ViewComposition
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.core.withContext
import com.ivianuu.essentials.ui.compose.view.FrameLayout
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.ImageView
import com.ivianuu.essentials.ui.compose.view.MATCH_PARENT
import com.ivianuu.essentials.ui.compose.view.RelativeLayout
import com.ivianuu.essentials.ui.compose.view.WRAP_CONTENT
import com.ivianuu.essentials.ui.compose.view.alignParentLeft
import com.ivianuu.essentials.ui.compose.view.alignParentRight
import com.ivianuu.essentials.ui.compose.view.background
import com.ivianuu.essentials.ui.compose.view.centerVertical
import com.ivianuu.essentials.ui.compose.view.height
import com.ivianuu.essentials.ui.compose.view.id
import com.ivianuu.essentials.ui.compose.view.image
import com.ivianuu.essentials.ui.compose.view.margin
import com.ivianuu.essentials.ui.compose.view.onClick
import com.ivianuu.essentials.ui.compose.view.padding
import com.ivianuu.essentials.ui.compose.view.size
import com.ivianuu.essentials.ui.compose.view.toLeftOf
import com.ivianuu.essentials.ui.compose.view.toRightOf
import com.ivianuu.essentials.ui.compose.view.viewId
import com.ivianuu.essentials.ui.compose.view.width
import com.ivianuu.essentials.ui.compose.view.wrapContent
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
    onClick: () -> Unit
) {
    ImageView {
        size(40.dp)
        padding(8.dp)
        image(image)
        +withContext {
            background(drawable = drawableAttr(R.attr.selectableItemBackgroundBorderless))
        }
        onClick(onClick)
    }
}