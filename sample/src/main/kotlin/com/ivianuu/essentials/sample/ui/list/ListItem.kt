package com.ivianuu.essentials.sample.ui.list

import androidx.compose.ViewComposition
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.view.ChainStyle
import com.ivianuu.essentials.ui.compose.view.ConstraintLayout
import com.ivianuu.essentials.ui.compose.view.FrameLayout
import com.ivianuu.essentials.ui.compose.view.MatchParent
import com.ivianuu.essentials.ui.compose.view.ParentId
import com.ivianuu.essentials.ui.compose.view.WrapContent
import com.ivianuu.essentials.ui.compose.view.backgroundColor
import com.ivianuu.essentials.ui.compose.view.centerVerticalIn
import com.ivianuu.essentials.ui.compose.view.constraintBottomToBottomOf
import com.ivianuu.essentials.ui.compose.view.constraintBottomToTopOf
import com.ivianuu.essentials.ui.compose.view.constraintLeftToLeftOf
import com.ivianuu.essentials.ui.compose.view.constraintLeftToRightOf
import com.ivianuu.essentials.ui.compose.view.constraintRightToLeftOf
import com.ivianuu.essentials.ui.compose.view.constraintRightToRightOf
import com.ivianuu.essentials.ui.compose.view.constraintTopToBottomOf
import com.ivianuu.essentials.ui.compose.view.constraintTopToTopOf
import com.ivianuu.essentials.ui.compose.view.constraintVerticalChainStyle
import com.ivianuu.essentials.ui.compose.view.height
import com.ivianuu.essentials.ui.compose.view.horizontalMargin
import com.ivianuu.essentials.ui.compose.view.id
import com.ivianuu.essentials.ui.compose.view.margin
import com.ivianuu.essentials.ui.compose.view.width
import com.ivianuu.essentials.ui.compose.view.wrapContent
import com.ivianuu.essentials.ui.compose.viewId

private val leadingId = viewId()
private val titleId = viewId()
private val subtitleId = viewId()
private val trailingId = viewId()

fun ViewComposition.ListItem(
    leading: (ViewComposition.() -> Unit)? = null,
    title: (ViewComposition.() -> Unit)? = null,
    subtitle: (ViewComposition.() -> Unit)? = null,
    trailing: (ViewComposition.() -> Unit)? = null,
    contentPadding: Dp = 16.dp
) {
    ConstraintLayout {
        width(MatchParent)
        height(WrapContent)
        // todo rippleBackground(bounded = true)
        backgroundColor(Color.Red)

        if (leading != null) {
            FrameLayout {
                id(leadingId)
                wrapContent()
                constraintLeftToLeftOf(ParentId)
                centerVerticalIn(ParentId)

                backgroundColor(Color.Yellow)


                margin(left = contentPadding)

                leading()
            }
        }

        if (title != null) {
            FrameLayout {
                id(titleId)
                width(MatchParent)
                height(WrapContent)

                backgroundColor(Color.Blue)

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

                constraintTopToTopOf(ParentId)
                if (subtitle != null) {
                    constraintBottomToTopOf(subtitleId)
                    constraintVerticalChainStyle(ChainStyle.Packed)
                } else {
                    constraintBottomToBottomOf(ParentId)
                }

                horizontalMargin(contentPadding)

                CurrentTextStyleProvider(+themeTextStyle { subtitle1 }) {
                    title()
                }
            }
        }

        if (subtitle != null) {
            FrameLayout {
                id(subtitleId)
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

                constraintBottomToBottomOf(ParentId)
                if (title != null) {
                    constraintTopToBottomOf(titleId)
                    constraintVerticalChainStyle(ChainStyle.Packed)
                } else {
                    constraintTopToTopOf(ParentId)
                }

                horizontalMargin(contentPadding)

                CurrentTextStyleProvider(+themeTextStyle { body2 }) {
                    subtitle()
                }
            }
        }

        if (trailing != null) {
            FrameLayout {
                id(trailingId)
                wrapContent()
                constraintRightToRightOf(ParentId)
                centerVerticalIn(ParentId)

                margin(right = contentPadding)

                trailing()
            }
        }
    }
}