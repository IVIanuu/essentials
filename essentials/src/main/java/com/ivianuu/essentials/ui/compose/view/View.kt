package com.ivianuu.essentials.ui.compose.view

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.updatePadding
import androidx.ui.core.Density
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.core.withDensity
import androidx.ui.graphics.Color
import androidx.ui.layout.EdgeInsets

fun <T : View> ViewDsl<T>.alpha(alpha: Float) {
    set(alpha) { this.alpha = it }
}

fun <T : View> ViewDsl<T>.background(
    drawable: Drawable? = null,
    color: Color? = null
) {
    set(drawable) { this.background = it }
    set(color) { setBackgroundColor(color?.toArgb() ?: 0) }
}

fun <T : View> ViewDsl<T>.elevation(elevation: Dp) {
    set(elevation) {
        withDensity(Density(context)) {
            this@set.elevation = elevation.toPx().value
        }
    }
}

fun <T : View> ViewDsl<T>.clickable(clickable: Boolean) {
    set(clickable) { this.isClickable = clickable }
}

fun <T : View> ViewDsl<T>.enabled(enabled: Boolean) {
    set(enabled) { this.isEnabled = enabled }
}

fun <T : View> ViewDsl<T>.focusable(focusable: Boolean) {
    set(focusable) { this.isFocusable = focusable }
}

fun <T : View> ViewDsl<T>.id(id: Int) {
    update { set(id) { } }
}

fun <T : View> ViewDsl<T>.minimumWidth(width: Dp) {
    set(width) {
        withDensity(Density(context)) {
            minimumWidth = it.toIntPx().value
        }
    }
}

fun <T : View> ViewDsl<T>.minimumHeight(height: Dp) {
    set(height) {
        withDensity(Density(context)) {
            minimumHeight = it.toIntPx().value
        }
    }
}

fun <T : View> ViewDsl<T>.minimumSize(size: Dp) {
    minimumWidth(size); minimumHeight(size)
}

fun <T : View> ViewDsl<T>.onClick(onClick: () -> Unit) {
    update { set(onClick) { setOnClickListener { onClick() } } }
}

fun <T : View> ViewDsl<T>.onLongClick(onLongClick: () -> Unit) {
    update { set(onLongClick) { setOnLongClickListener { onLongClick(); true } } }
}

fun <T : View> ViewDsl<T>.padding(padding: EdgeInsets) {
    set(padding) { (left, top, right, bottom) ->
        withDensity(Density(node.context)) {
            updatePadding(
                left = left.toIntPx().value,
                top = top.toIntPx().value,
                right = right.toIntPx().value,
                bottom = bottom.toIntPx().value
            )
        }
    }
}

fun <T : View> ViewDsl<T>.padding(
    left: Dp = 0.dp,
    top: Dp = 0.dp,
    right: Dp = 0.dp,
    bottom: Dp = 0.dp
) {
    padding(EdgeInsets(left, top, right, bottom))
}

fun <T : View> ViewDsl<T>.padding(padding: Dp) {
    padding(padding, padding, padding, padding)
}

fun <T : View> ViewDsl<T>.horizontalPadding(padding: Dp) {
    padding(left = padding, right = padding)
}

fun <T : View> ViewDsl<T>.verticalPadding(padding: Dp) {
    padding(top = padding, bottom = padding)
}

/*
// todo
fun BuildContext.Pivot(
    x: Float,
    y: Float,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    updateViewProps = { it.pivotX = x; it.pivotY = y },
    child = child
)

fun BuildContext.Rotation(rotation: Float, child: BuildContext.() -> Unit) =
    ViewPropsWidget(value = rotation, prop = View::setRotation, child = child)

fun BuildContext.Scale(
    x: Float,
    y: Float,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    updateViewProps = { it.scaleX = x; it.scaleY = y },
    child = child
)

fun BuildContext.Translation(
    x: Float,
    y: Float,
    z: Float,
    child: BuildContext.() -> Unit
) = ViewPropsWidget(
    updateViewProps = {
        it.translationX = x;
        it.translationY = y;
        it.translationZ = z
    },
    child = child
)*/

enum class Visibility {
    Visible, Invisible, Gone;

    fun toVisibilityInt() = when (this) {
        Visible -> View.VISIBLE
        Invisible -> View.INVISIBLE
        Gone -> View.GONE
    }
}

fun <T : View> ViewDsl<T>.visibility(visibility: Visibility) {
    set(visibility) { this.visibility = it.toVisibilityInt() }
}

fun <T : View> ViewDsl<T>.visible() = visibility(Visibility.Visible)

fun <T : View> ViewDsl<T>.invisible() = visibility(Visibility.Invisible)

fun <T : View> ViewDsl<T>.gone() = visibility(Visibility.Gone)