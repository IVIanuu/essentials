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

package com.ivianuu.essentials.ui.compose.layout

import androidx.compose.Composable
import androidx.constraintlayout.solver.state.ConstraintReference
import androidx.constraintlayout.solver.state.Dimension
import androidx.constraintlayout.solver.state.State
import androidx.constraintlayout.solver.state.helpers.AlignHorizontallyReference
import androidx.constraintlayout.solver.state.helpers.AlignVerticallyReference
import androidx.constraintlayout.solver.state.helpers.BarrierReference
import androidx.constraintlayout.solver.state.helpers.GuidelineReference
import androidx.constraintlayout.solver.state.helpers.HorizontalChainReference
import androidx.constraintlayout.solver.state.helpers.VerticalChainReference
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer
import androidx.constraintlayout.solver.widgets.Optimizer
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure
import androidx.ui.core.Constraints
import androidx.ui.core.Dp
import androidx.ui.core.FirstBaseline
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.core.Placeable.PlacementScope.place

/**
 * Implementation of ConstraintLayout in Compose
 */

private val Measurable.id: String get() = (parentData as ConstraintLayoutInfo).id

private data class ConstraintLayoutInfo(val id: String, var placeable: Placeable? = null)

class ConstraintLayoutContext internal constructor() : BasicMeasure.Measurer {
    internal val childrenList = mutableMapOf<String, @Composable() () -> Unit>()
    var constraintSet: ConstraintSet? = null

    var root: ConstraintWidgetContainer = ConstraintWidgetContainer(0, 0)

    init {
        root.measurer = this
    }

    override fun didMeasures() {
        // nothing
    }

    override fun measure(constraintWidget: ConstraintWidget, measure: BasicMeasure.Measure) {
        if (constraintWidget.companionWidget == null) {
            return
        }
        if (constraintWidget.companionWidget !is Measurable) {
            return
        }
        val component = constraintWidget.companionWidget as Measurable
        var measuredWidth = constraintWidget.width
        var measuredHeight = constraintWidget.height
        var constraints = Constraints()

        if (measure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            && measure.verticalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
        ) {
            constraints = androidx.ui.core.Constraints()
        } else if (measure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            constraints = androidx.ui.core.Constraints
                .tightConstraintsForHeight(IntPx(constraintWidget.height))
        } else if (measure.verticalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            constraints =
                androidx.ui.core.Constraints.tightConstraintsForWidth(IntPx(constraintWidget.width))
        } else {
            constraints = androidx.ui.core.Constraints.tightConstraints(
                IntPx(constraintWidget.width),
                IntPx(constraintWidget.height)
            )
        }

        var placeable = component.measure(constraints)
        (component.parentData as ConstraintLayoutInfo).placeable = placeable

        if (measure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            measuredWidth = placeable.width.value
        }
        if (measure.verticalBehavior == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            measuredHeight = placeable.height.value
        }

        measure.measuredWidth = measuredWidth
        measure.measuredHeight = measuredHeight
        var baseline = placeable.get(FirstBaseline)
        if (baseline != null) {
            measure.measuredBaseline = baseline.value
        } else {
            measure.measuredBaseline = measuredHeight
        }
    }

    fun id(id: String, child: @Composable() () -> Unit) {
        childrenList[id] = {
            ParentData(ConstraintLayoutInfo(id = id), child)
        }
    }

    fun map(child: Measurable) {
        constraintSet?.map(child.id, child)
    }

    fun ConstraintSet(constraintSet: ConstraintSet.() -> Unit) {
        this.constraintSet = com.ivianuu.essentials.ui.compose.layout.ConstraintSet(constraintSet)
    }

    fun layout(layoutWidth: IntPx, layoutHeight: IntPx) {
        val width = layoutWidth.value
        val height = layoutHeight.value
        constraintSet?.width = Dimension.Fixed(width)
        constraintSet?.height = Dimension.Fixed(height)
        constraintSet?.apply(root, false)
        root.measure(
            Optimizer.OPTIMIZATION_NONE, BasicMeasure.EXACTLY, width, BasicMeasure.EXACTLY, height,
            0, 0, 0, 0
        )

        for (child in root.children) {
            if (child.companionWidget == null) {
                continue
            }
            val measurable = child.companionWidget as Measurable
            var placeable = (measurable.parentData as ConstraintLayoutInfo).placeable
            placeable?.place(IntPx(child.x), IntPx(child.y))
        }
    }

}

@Suppress("FunctionName")
@Composable
fun ConstraintLayout(
    children: ConstraintLayoutContext.() -> Unit
) {
    var scope = ConstraintLayoutContext()
    with(scope) {
        children()
    }

    var clChildren: @Composable() () -> Unit = @Composable {
        scope.childrenList.forEach { it.value() }
    }

    Layout(clChildren) { measurables, outerConstraints ->
        var layoutWidth = outerConstraints.maxWidth
        var layoutHeight = outerConstraints.maxHeight

        measurables.forEach { child ->
            scope.map(child)
        }
        layout(layoutWidth, layoutHeight) {
            scope.layout(layoutWidth, layoutHeight)
        }
    }
}

/**
 * ConstraintSet implementation for Compose
 */
class ConstraintSet(private var block: ConstraintSet.() -> Unit) {

    val PARENT = State.PARENT

    val WRAP_DIMENSION = Dimension.WRAP_DIMENSION
    val SPREAD_DIMENSION = Dimension.SPREAD_DIMENSION
    val PARENT_DIMENSION = Dimension.PARENT_DIMENSION

    val CHAIN_SPREAD = State.Chain.SPREAD
    val CHAIN_SPREAD_INSIDE = State.Chain.SPREAD
    val CHAIN_PACKED = State.Chain.PACKED

    var width: Dimension = Dimension.Fixed(PARENT_DIMENSION)
    var height: Dimension = Dimension.Fixed(PARENT_DIMENSION)

    var state = ConstraintLayoutState()

    fun constraints(key: Any, constraints: Constraints.() -> Unit) {
        val reference = state.constraints(key) as Constraints
        reference.constraints()
    }

    fun horizontalGuideline(key: Any, constraints: GuidelineReference.() -> Unit) {
        val guidelineReference = state.horizontalGuideline(key)
        constraints.invoke(guidelineReference)
    }

    fun verticalGuideline(key: Any, constraints: GuidelineReference.() -> Unit) {
        val guidelineReference = state.verticalGuideline(key)
        constraints.invoke(guidelineReference)
    }

    fun leftBarrier(key: Any, constraints: (BarrierReference.() -> Unit)? = null) {
        val barrierReference = state.barrier(key, State.Direction.LEFT)
        constraints?.invoke(barrierReference)
    }

    fun startBarrier(key: Any, constraints: (BarrierReference.() -> Unit)? = null) {
        val barrierReference = state.barrier(key, State.Direction.START)
        constraints?.invoke(barrierReference)
    }

    fun rightBarrier(key: Any, constraints: (BarrierReference.() -> Unit)? = null) {
        val barrierReference = state.barrier(key, State.Direction.RIGHT)
        constraints?.invoke(barrierReference)
    }

    fun endBarrier(key: Any, constraints: (BarrierReference.() -> Unit)? = null) {
        val barrierReference = state.barrier(key, State.Direction.END)
        constraints?.invoke(barrierReference)
    }

    fun topBarrier(key: Any, constraints: (BarrierReference.() -> Unit)? = null) {
        val barrierReference = state.barrier(key, State.Direction.TOP)
        constraints?.invoke(barrierReference)
    }

    fun bottomBarrier(key: Any, constraints: (BarrierReference.() -> Unit)? = null) {
        val barrierReference = state.barrier(key, State.Direction.BOTTOM)
        constraints?.invoke(barrierReference)
    }

    fun horizontalChain(
        vararg references: Any,
        constraints: (HorizontalChainReference.() -> Unit)? = null
    ) {
        val horizontalChainReference = state.horizontalChain(*references)
        constraints?.invoke(horizontalChainReference)
        horizontalChainReference.apply()
    }

    fun verticalChain(
        vararg references: Any,
        constraints: (VerticalChainReference.() -> Unit)? = null
    ) {
        val verticalChainReference = state.verticalChain(*references)
        constraints?.invoke(verticalChainReference)
        verticalChainReference.apply()
    }

    fun centerHorizontally(
        vararg references: Any,
        constraints: (AlignHorizontallyReference.() -> Unit)? = null
    ) {
        val centerHorizontallyReference = state.centerHorizontally(*references)
        constraints?.invoke(centerHorizontallyReference)
        centerHorizontallyReference.apply()
    }

    fun centerVertically(
        vararg references: Any,
        constraints: (AlignVerticallyReference.() -> Unit)? = null
    ) {
        val centerVerticallyReference = state.centerVertically(*references)
        constraints?.invoke(centerVerticallyReference)
        centerVerticallyReference.apply()
    }

    fun map(key: Any, value: Any) {
        state.map(key, value)
    }

    fun apply(root: ConstraintWidgetContainer, automap: Boolean = true) {
        state.reset()
        state.width(width)
        state.height(height)
        block.invoke(this)
        state.apply(root)
        if (automap) {
            state.directMapping()
        }
    }

    fun directMapping() {
        state.directMapping()
    }
}

class ConstraintLayoutState : State() {
    override fun convertDimension(value: Any?): Int {
        if (value is Dp) {
            return value.value.toInt()
        }
        return super.convertDimension(value)
    }

    override fun createConstraintReference(key: Any?): ConstraintReference {
        return Constraints(this)
    }
}

class Constraints(state: State) : ConstraintReference(state) {
    // nothing here but could redefine DSL / add methods
}