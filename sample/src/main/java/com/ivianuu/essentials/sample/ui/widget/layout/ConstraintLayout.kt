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
import com.ivianuu.essentials.sample.ui.widget.layout.ConstraintSetBuilder.Connection.BasicConnection
import com.ivianuu.essentials.sample.ui.widget.layout.ConstraintSetBuilder.Side
import com.ivianuu.essentials.sample.ui.widget.lib.*

val PARENT_ID = ConstraintLayout.LayoutParams.PARENT_ID

fun BuildContext.ConstraintLayout(children: ConstraintLayoutChildren.() -> Unit): Widget =
    StatelessWidget {
        val constraintSetBuilder = ConstraintSetBuilder()
        +ViewGroupWidget<ConstraintLayout>(
            updateView = { constraintSetBuilder.applyTo(it) },
            children = {
                ConstraintLayoutChildren(this, constraintSetBuilder)
                    .apply(children)
            }
        )

    }

class ConstraintLayoutChildren(
    private val context: BuildContext,
    private val constraintSetBuilder: ConstraintSetBuilder
) : BuildContext() {

    override val owner: BuildOwner?
        get() = context.owner
    override val widget: Widget
        get() = context.widget

    override fun add(id: Any, child: Widget) {
        context.add(id, child)
    }

    override fun <T> cache(calculation: () -> T): T = context.cache(calculation)

    override fun <T> cache(vararg inputs: Any?, calculation: () -> T): T =
        context.cache(inputs = *inputs, calculation = calculation)

    override fun <T> getAmbient(key: Ambient<T>): T? = context.getAmbient(key)

    fun constraints(block: ConstraintSetBuilder.() -> Unit) {
        constraintSetBuilder.apply(block)
    }

}

class ConstraintSetBuilder : ConstraintSet() {

    operator fun Int.invoke(init: ViewConstraintBuilder.() -> Unit) {
        ViewConstraintBuilder(this, this@ConstraintSetBuilder).apply(init)
    }

    infix fun Side.of(viewId: Int) = when (this) {
        Side.LEFT -> ViewSide.Left(viewId)
        Side.RIGHT -> ViewSide.Right(viewId)
        Side.TOP -> ViewSide.Top(viewId)
        Side.BOTTOM -> ViewSide.Bottom(viewId)
        Side.BASELINE -> ViewSide.Baseline(viewId)
        Side.START -> ViewSide.Start(viewId)
        Side.END -> ViewSide.End(viewId)
    }

    infix fun Pair<ViewSide, Side>.of(viewId: Int) = first to (second of viewId)

    infix fun ViewSide.to(targetSide: ViewSide) = BasicConnection(this, targetSide)

    infix fun BasicConnection.margin(margin: Int) = Connection.MarginConnection(from, to, margin)

    fun connect(vararg connections: Connection) {
        for (connection in connections) {
            when (connection) {
                is Connection.MarginConnection -> connect(
                    connection.from.viewId,
                    connection.from.sideId,
                    connection.to.viewId,
                    connection.to.sideId,
                    connection.margin
                )
                is BasicConnection -> connect(
                    connection.from.viewId,
                    connection.from.sideId,
                    connection.to.viewId,
                    connection.to.sideId
                )
            }
        }
    }

    enum class Side {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        BASELINE,
        START,
        END,
    }

    sealed class ViewSide(val viewId: Int) {
        class Left(viewId: Int) : ViewSide(viewId)
        class Right(viewId: Int) : ViewSide(viewId)
        class Top(viewId: Int) : ViewSide(viewId)
        class Bottom(viewId: Int) : ViewSide(viewId)
        class Baseline(viewId: Int) : ViewSide(viewId)
        class Start(viewId: Int) : ViewSide(viewId)
        class End(viewId: Int) : ViewSide(viewId)

        val sideId: Int
            get() = when (this) {
                is Left -> LEFT
                is Right -> RIGHT
                is Top -> TOP
                is Bottom -> BOTTOM
                is Baseline -> BASELINE
                is Start -> START
                is End -> END
            }
    }

    sealed class Connection(val from: ViewSide, val to: ViewSide) {
        class BasicConnection(from: ViewSide, to: ViewSide) : Connection(from, to)
        class MarginConnection(from: ViewSide, to: ViewSide, val margin: Int) : Connection(from, to)
    }
}

class ViewConstraintBuilder(
    private val viewId: Int,
    private val constraintSetBuilder: ConstraintSetBuilder
) {

    infix fun Side.to(targetSide: Side) = Pair(this, targetSide)

    infix fun Pair<Side, Side>.of(targetViewId: Int): BasicConnection =
        constraintSetBuilder.run { (first of viewId) to (second of targetViewId) }

    fun clear() {
        constraintSetBuilder.clear(viewId)
    }

    fun clear(sideId: Int) {
        constraintSetBuilder.clear(viewId, sideId)
    }

    fun setMargin(sideId: Int, value: Int) {
        constraintSetBuilder.setMargin(viewId, sideId, value)
    }

    fun setGoneMargin(sideId: Int, value: Int) {
        constraintSetBuilder.setGoneMargin(viewId, sideId, value)
    }

    fun horizontalBias(value: Float) {
        constraintSetBuilder.setHorizontalBias(viewId, value)
    }

    fun verticalBias(value: Float) {
        constraintSetBuilder.setVerticalBias(viewId, value)
    }

    fun horizontalWeight(value: Float) {
        constraintSetBuilder.setHorizontalWeight(viewId, value)
    }

    fun verticalWeight(value: Float) {
        constraintSetBuilder.setVerticalWeight(viewId, value)
    }

    fun horizontalChainStyle(value: Int) {
        constraintSetBuilder.setHorizontalChainStyle(viewId, value)
    }

    fun verticalChainStyle(value: Int) {
        constraintSetBuilder.setVerticalChainStyle(viewId, value)
    }

    fun height(value: Int) {
        constraintSetBuilder.constrainHeight(viewId, value)
    }

    fun width(value: Int) {
        constraintSetBuilder.constrainWidth(viewId, value)
    }

    fun minWidth(value: Int) {
        constraintSetBuilder.constrainMinWidth(viewId, value)
    }

    fun minHeight(value: Int) {
        constraintSetBuilder.constrainMinHeight(viewId, value)
    }

    fun maxWidth(value: Int) {
        constraintSetBuilder.constrainMaxWidth(viewId, value)
    }

    fun maxHeight(value: Int) {
        constraintSetBuilder.constrainMaxHeight(viewId, value)
    }

    fun defaultWidth(value: Int) {
        constraintSetBuilder.constrainDefaultWidth(viewId, value)
    }

    fun defaultHeight(value: Int) {
        constraintSetBuilder.constrainDefaultHeight(viewId, value)
    }

}