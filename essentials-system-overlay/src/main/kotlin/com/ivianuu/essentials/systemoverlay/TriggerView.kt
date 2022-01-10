/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import android.annotation.*
import android.view.*
import android.widget.*
import com.ivianuu.essentials.*

@SuppressLint("ViewConstructor")
class TriggerView(private val delegate: View) : FrameLayout(delegate.context) {
  var useDownTouchOffset = true

  private val thisDownLocation = intArrayOf(0, 0)
  private val thisLocation = intArrayOf(0, 0)
  private val delegateLocation = intArrayOf(0, 0)

  @SuppressLint("ClickableViewAccessibility")
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    getLocationOnScreen(thisLocation)
    delegate.getLocationOnScreen(delegateLocation)

    if (useDownTouchOffset) {
      if (ev.action == MotionEvent.ACTION_DOWN) {
        // capture down location
        thisDownLocation[0] = thisLocation[0]
        thisDownLocation[1] = thisLocation[1]
      } else {
        // translate to down location view
        ev.offsetLocation(
          (thisLocation[0] - thisDownLocation[0]).toFloat(),
          (thisLocation[1] - thisDownLocation[1]).toFloat()
        )
      }

      // translate to delegate view
      ev.offsetLocation(
        (thisDownLocation[0] - delegateLocation[0]).toFloat(),
        (thisDownLocation[1] - delegateLocation[1]).toFloat()
      )
    } else {
      // translate to delegate view
      ev.offsetLocation(
        (thisLocation[0] - delegateLocation[0]).toFloat(),
        (thisLocation[1] - delegateLocation[1]).toFloat()
      )
    }

    // compose crashes in some situations
    return catch { delegate.dispatchTouchEvent(ev) }
      .onFailure { it.printStackTrace() }
      .getOrElse { false }
  }
}
