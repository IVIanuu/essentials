/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.systemoverlay

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.catch

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
