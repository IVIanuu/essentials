package com.ivianuu.essentials.systemoverlay

import android.annotation.*
import android.view.*
import android.widget.*
import com.github.michaelbull.result.*

@SuppressLint("ViewConstructor")
class TriggerView(private val delegate: View) : FrameLayout(delegate.context) {
    @SuppressLint("ClickableViewAccessibility")
    override fun dispatchTouchEvent(event: MotionEvent): Boolean =
        // compose crashes in some situations
        runCatching { delegate.dispatchTouchEvent(event) }
            .getOrElse { false }
}
