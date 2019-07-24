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

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget

val PARENT_ID = ConstraintLayout.LayoutParams.PARENT_ID

fun ConstraintLayout(
    key: Any? = null,
    children: ConstraintLayoutChildren.() -> Unit
): Widget = StatelessWidget("ConstraintLayout") {
    val constraintSet = ConstraintSet()

    +ViewGroupWidget<ConstraintLayout>(
        key = key,
        updateView = { constraintSet.applyTo(it) },
        children = {
            ConstraintLayoutChildren(this, constraintSet)
                .apply(children)
        }
    )

}

class ConstraintLayoutChildren(
    private val context: BuildContext,
    private val constraintSet: ConstraintSet
) : BuildContext by context {

    fun constraints(block: Constraints.() -> Unit) {
        Constraints(constraintSet).apply(block)
    }

    class Constraints(val constraintSet: ConstraintSet) {

        operator fun Int.invoke(block: ChildConstraints.() -> Unit) {
            ChildConstraints(this, constraintSet).apply(block)
        }

        class ChildConstraints(
            val id: Int,
            val constraintSet: ConstraintSet
        ) {

            fun horizontalBias(horizontalBias: Float) {
                constraintSet.setHorizontalBias(id, horizontalBias)
            }

            fun verticalBias(verticalBias: Float) {
                constraintSet.setVerticalBias(id, verticalBias)
            }

        }

    }
}