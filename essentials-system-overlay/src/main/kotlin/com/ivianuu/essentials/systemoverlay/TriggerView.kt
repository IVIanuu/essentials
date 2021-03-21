package com.ivianuu.essentials.systemoverlay

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

@SuppressLint("ViewConstructor")
class TriggerView(private val delegate: View) : FrameLayout(delegate.context) {
    @SuppressLint("ClickableViewAccessibility")
    override fun dispatchTouchEvent(event: MotionEvent): Boolean =
        delegate.dispatchTouchEvent(event)
}
