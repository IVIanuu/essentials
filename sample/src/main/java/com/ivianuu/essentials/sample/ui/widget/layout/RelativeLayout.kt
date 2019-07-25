/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample.ui.widget.layout

import android.view.Gravity
import android.widget.RelativeLayout
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget

fun BuildContext.RelativeLayout(
    gravity: Int = Gravity.START or Gravity.TOP,
    children: BuildContext.() -> Unit
) = ViewGroupWidget<RelativeLayout>(
    updateView = { view -> view.gravity = gravity },
    children = children
)

/**
class RelativeLayoutChildren(private val buildContext: BuildContext) : BuildContext by buildContext {

private val rulesByViewId = mutableListOf<Int>()

private val pending = mutableListOf<Widget>()

override fun add(child: Widget) {

}

}

data class RelativeLayoutRule(val verb: Int, val subject: Int?)

class RelativeLpBuilder(val viewId: Int) {

private val rules = mutableListOf<RelativeLayoutRule>()

fun topOf(id: Int) = addRule(ABOVE, id)

fun above(id: Int) = addRule(ABOVE, id)

fun below(id: Int) = addRule(BELOW, id)

fun bottomOf(id: Int) = addRule(BELOW, id)

fun leftOf(id: Int) = addRule(LEFT_OF, id)

fun startOf(id: Int): Unit = addRule(START_OF, id)

fun rightOf(id: Int) = addRule(RIGHT_OF, id)

fun endOf(id: Int): Unit = addRule(END_OF, id)

fun sameLeft(id: Int) = addRule(ALIGN_LEFT, id)

fun sameStart(id: Int): Unit = addRule(ALIGN_START, id)

fun sameTop(id: Int) = addRule(ALIGN_TOP, id)

fun sameRight(id: Int) = addRule(ALIGN_RIGHT, id)

fun sameEnd(id: Int): Unit = addRule(ALIGN_END, id)

fun sameBottom(id: Int) = addRule(ALIGN_BOTTOM, id)

fun alignStart(id: Int): Unit = addRule(ALIGN_START, id)

fun alignEnd(id: Int): Unit = addRule(ALIGN_END, id)

fun alignParentTop() = addRule(ALIGN_PARENT_TOP)

fun alignParentRight() = addRule(ALIGN_PARENT_RIGHT)

fun alignParentBottom() = addRule(ALIGN_PARENT_BOTTOM)

fun alignParentLeft() = addRule(ALIGN_PARENT_LEFT)

fun centerHorizontally() = addRule(CENTER_HORIZONTAL)

fun centerVertically() = addRule(CENTER_VERTICAL)

fun centerInParent() = addRule(CENTER_IN_PARENT)

fun alignParentStart(): Unit = addRule(ALIGN_PARENT_START)

fun alignParentEnd(): Unit = addRule(ALIGN_PARENT_END)

fun baselineOf(id: Int) = addRule(ALIGN_BASELINE, id)

fun addRule(verb: Int, subject: Int? = null) {
rules.add(RelativeLayoutRule(verb, subject))
}

fun reset() {
rules.clear()
}
}*/