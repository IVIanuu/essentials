package com.ivianuu.essentials.ui.compose.view

import android.view.View
import androidx.compose.ViewComposition
import androidx.constraintlayout.widget.ConstraintLayout
import com.ivianuu.essentials.ui.compose.sourceLocation

val ParentId = ConstraintLayout.LayoutParams.PARENT_ID

inline fun ViewComposition.ConstraintLayout(noinline block: ViewDsl<ConstraintLayout>.() -> Unit) =
    ConstraintLayout(sourceLocation(), block)

fun ViewComposition.ConstraintLayout(key: Any, block: ViewDsl<ConstraintLayout>.() -> Unit) =
    View(key, { ConstraintLayout(it) }, block)

fun <T : View> ViewDsl<T>.constraintLeftToLeftOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        startToStart = id
    }
}

fun <T : View> ViewDsl<T>.constraintLeftToRightOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        startToEnd = id
    }
}

fun <T : View> ViewDsl<T>.constraintTopToTopOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        topToTop = id
    }
}

fun <T : View> ViewDsl<T>.constraintTopToBottomOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        topToBottom = id
    }
}

fun <T : View> ViewDsl<T>.constraintRightToLeftOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        endToStart = id
    }
}

fun <T : View> ViewDsl<T>.constraintRightToRightOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        endToEnd = id
    }
}

fun <T : View> ViewDsl<T>.constraintBottomToTopOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        bottomToTop = id
    }
}

fun <T : View> ViewDsl<T>.constraintBottomToBottomOf(id: Int) {
    setLayoutParams(id) {
        this as ConstraintLayout.LayoutParams
        bottomToBottom = id
    }
}

fun <T : View> ViewDsl<T>.centerIn(id: Int) {
    centerHorizontalIn(id)
    centerVerticalIn(id)
}

fun <T : View> ViewDsl<T>.centerHorizontalIn(id: Int) {
    constraintLeftToLeftOf(id)
    constraintRightToRightOf(id)
    constraintHorizontalBias(0.5f)
}

fun <T : View> ViewDsl<T>.centerVerticalIn(id: Int) {
    constraintTopToTopOf(id)
    constraintBottomToBottomOf(id)
    constraintVerticalBias(0.5f)
}

fun <T : View> ViewDsl<T>.constraintHorizontalBias(value: Float) {
    setLayoutParams(value) {
        this as ConstraintLayout.LayoutParams
        horizontalBias = value
    }
}

fun <T : View> ViewDsl<T>.constraintVerticalBias(value: Float) {
    setLayoutParams(value) {
        this as ConstraintLayout.LayoutParams
        verticalBias = value
    }
}

enum class ChainStyle {
    Packed, Spread, SpreadInside;

    fun toChainStyleInt() = when (this) {
        Packed -> ConstraintLayout.LayoutParams.CHAIN_SPREAD
        Spread -> ConstraintLayout.LayoutParams.CHAIN_SPREAD
        SpreadInside -> ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE
    }

}

fun <T : View> ViewDsl<T>.constraintHorizontalChainStyle(value: ChainStyle) {
    setLayoutParams(value) {
        this as ConstraintLayout.LayoutParams
        horizontalChainStyle = value.toChainStyleInt()
    }
}

fun <T : View> ViewDsl<T>.constraintVerticalChainStyle(value: ChainStyle) {
    setLayoutParams(value) {
        this as ConstraintLayout.LayoutParams
        verticalChainStyle = value.toChainStyleInt()
    }
}

fun <T : View> ViewDsl<T>.constraintHorizontalWeight(value: Float) {
    setLayoutParams(value) {
        this as ConstraintLayout.LayoutParams
        horizontalWeight = value
    }
}

fun <T : View> ViewDsl<T>.constraintVerticalWeight(value: Float) {
    setLayoutParams(value) {
        this as ConstraintLayout.LayoutParams
        verticalWeight = value
    }
}