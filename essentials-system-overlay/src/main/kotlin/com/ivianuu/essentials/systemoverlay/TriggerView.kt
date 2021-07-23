package com.ivianuu.essentials.systemoverlay

import android.annotation.*
import android.view.*
import android.widget.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*

@SuppressLint("ViewConstructor")
class TriggerView(private val delegate: View) : FrameLayout(delegate.context) {
  private val delegateLocation = intArrayOf(0, 0)
  private val thisLocation = intArrayOf(0, 0)

  @SuppressLint("ClickableViewAccessibility")
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    delegate.getLocationOnScreen(delegateLocation)
    getLocationOnScreen(thisLocation)
    ev.offsetLocation(
      (thisLocation[0] - delegateLocation[0]).toFloat(),
      (thisLocation[1] - delegateLocation[1]).toFloat()
    )
    // compose crashes in some situations
    return catch { delegate.dispatchTouchEvent(ev) }
      .onFailure { it.printStackTrace() }
      .getOrElse { false }
  }
}
