package com.ivianuu.essentials.systemoverlay

import android.annotation.*
import android.view.*
import android.widget.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*

@SuppressLint("ViewConstructor")
class TriggerView(private val delegate: View) : FrameLayout(delegate.context) {
    @SuppressLint("ClickableViewAccessibility")
    override fun dispatchTouchEvent(event: MotionEvent): Boolean =
        // compose crashes in some situations
        catch { delegate.dispatchTouchEvent(event) }
            .getOrElse { false }
}
