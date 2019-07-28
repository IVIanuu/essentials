package com.ivianuu.essentials.ui.compose.view

import android.view.View
import android.view.ViewGroup
import androidx.compose.unaryPlus
import androidx.core.view.updateMargins
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.core.withDensity
import androidx.ui.layout.EdgeInsets

fun <T : View> ViewDsl<T>.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
    node.getLayoutParamsBuilder().add(block)
}

// todo find a better name
inline fun <T : View, reified V> ViewDsl<T>.setLayoutParams(
    value: V,
    crossinline block: ViewGroup.LayoutParams.(V) -> Unit
) {
    update {
        set(value) {
            getLayoutParamsBuilder().add { block(value) }
        }
    }
}

fun <T : View> ViewDsl<T>.width(width: Dp) {
    +withDensity {
        setLayoutParams(width) {
            this@setLayoutParams.width = when (it) {
                Dp.MATCH_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
                Dp.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
                else -> it.toIntPx().value
            }
        }
    }
}

fun <T : View> ViewDsl<T>.height(height: Dp) {
    +withDensity {
        setLayoutParams(height) {
            this@setLayoutParams.height = when (it) {
                Dp.MATCH_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
                Dp.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
                else -> it.toIntPx().value
            }
        }
    }
}

fun <T : View> ViewDsl<T>.size(size: Dp) {
    width(size)
    height(size)
}

val Dp.Companion.MATCH_PARENT get() = ViewGroup.LayoutParams.MATCH_PARENT.dp
val Dp.Companion.WRAP_CONTENT get() = ViewGroup.LayoutParams.WRAP_CONTENT.dp

fun <T : View> ViewDsl<T>.matchParent() {
    size(Dp.MATCH_PARENT)
}

fun <T : View> ViewDsl<T>.wrapContent() {
    size(Dp.WRAP_CONTENT)
}

fun <T : View> ViewDsl<T>.margin(margin: EdgeInsets) {
    +withDensity {
        setLayoutParams(margin) { (left, top, right, bottom) ->
            this as ViewGroup.MarginLayoutParams
            updateMargins(
                left = left.toIntPx().value,
                top = top.toIntPx().value,
                right = right.toIntPx().value,
                bottom = bottom.toIntPx().value
            )
        }
    }
}

fun <T : View> ViewDsl<T>.margin(
    left: Dp = 0.dp,
    top: Dp = 0.dp,
    right: Dp = 0.dp,
    bottom: Dp = 0.dp
) {
    margin(EdgeInsets(left, top, right, bottom))
}

fun <T : View> ViewDsl<T>.margin(margin: Dp) {
    margin(margin, margin, margin, margin)
}

fun <T : View> ViewDsl<T>.horizontalMargin(margin: Dp) {
    margin(left = margin, right = margin)
}

fun <T : View> ViewDsl<T>.verticalMargin(margin: Dp) {
    margin(top = margin, bottom = margin)
}