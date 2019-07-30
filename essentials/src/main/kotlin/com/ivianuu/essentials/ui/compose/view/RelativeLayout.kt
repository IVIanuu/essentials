package com.ivianuu.essentials.ui.compose.view

import android.view.View
import android.widget.RelativeLayout
import android.widget.RelativeLayout.*
import androidx.compose.ViewComposition
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.RelativeLayout(noinline block: ViewDsl<RelativeLayout>.() -> Unit) =
    RelativeLayout(sourceLocation(), block)

fun ViewComposition.RelativeLayout(key: Any, block: ViewDsl<RelativeLayout>.() -> Unit) =
    View(key, { RelativeLayout(it) }, block)

fun <T : View> ViewDsl<T>.relativeLayoutRule(verb: Int, subject: Int) {
    setLayoutParams(arrayOf(verb, subject)) {
        this as RelativeLayout.LayoutParams
        addRule(verb, subject)
    }
}

fun <T : View> ViewDsl<T>.toLeftOf(id: Int) = relativeLayoutRule(START_OF, id)
fun <T : View> ViewDsl<T>.toRightOf(id: Int) = relativeLayoutRule(END_OF, id)

fun <T : View> ViewDsl<T>.above(id: Int) = relativeLayoutRule(ABOVE, id)
fun <T : View> ViewDsl<T>.below(id: Int) = relativeLayoutRule(BELOW, id)

fun <T : View> ViewDsl<T>.alignBaseline(id: Int) = relativeLayoutRule(ALIGN_BASELINE, id)

fun <T : View> ViewDsl<T>.alignLeft(id: Int) = relativeLayoutRule(ALIGN_START, id)
fun <T : View> ViewDsl<T>.alignTop(id: Int) = relativeLayoutRule(ALIGN_TOP, id)
fun <T : View> ViewDsl<T>.alignRight(id: Int) = relativeLayoutRule(ALIGN_RIGHT, id)
fun <T : View> ViewDsl<T>.alignBottom(id: Int) = relativeLayoutRule(ALIGN_BOTTOM, id)

fun <T : View> ViewDsl<T>.alignParentLeft() = relativeLayoutRule(ALIGN_PARENT_START, TRUE)
fun <T : View> ViewDsl<T>.alignParenTop() = relativeLayoutRule(ALIGN_PARENT_TOP, TRUE)
fun <T : View> ViewDsl<T>.alignParentRight() = relativeLayoutRule(ALIGN_PARENT_END, TRUE)
fun <T : View> ViewDsl<T>.alignParentBottom() = relativeLayoutRule(ALIGN_PARENT_BOTTOM, TRUE)

fun <T : View> ViewDsl<T>.centerHorizontal() = relativeLayoutRule(CENTER_HORIZONTAL, TRUE)
fun <T : View> ViewDsl<T>.centerVertical() = relativeLayoutRule(CENTER_VERTICAL, TRUE)
fun <T : View> ViewDsl<T>.centerInParent() = relativeLayoutRule(CENTER_IN_PARENT, TRUE)