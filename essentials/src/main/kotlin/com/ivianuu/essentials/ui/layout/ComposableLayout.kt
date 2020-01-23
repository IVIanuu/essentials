package com.ivianuu.essentials.ui.layout

import android.content.Context
import androidx.compose.Composable
import androidx.compose.Compose
import androidx.compose.CompositionReference
import androidx.compose.composer
import androidx.compose.compositionReference
import androidx.compose.remember
import androidx.ui.core.*
import com.ivianuu.essentials.ui.core.current

typealias ComposableMeasureBlock = MeasureScope.(
    constraints: Constraints,
    getMeasurables: () -> List<Measurable>,
    recompose: () -> Unit
) -> MeasureScope.LayoutResult

@Composable
fun ComposableLayout(
    modifier: Modifier = Modifier.None,
    children: @Composable () -> Unit,
    measureBlock: ComposableMeasureBlock
) {
    val state = remember { ComposableLayoutState() }
    state.children = children
    state.context = ContextAmbient.current
    state.compositionRef = compositionReference()
    state.measureBlock = measureBlock

    composer.emit<LayoutNode>(
        key = 0,
        ctor = { LayoutNode() },
        update = {
            node.modifier = modifier
            node.ref = state.nodeRef
            node.measureBlocks = state.measureBlocks
        }
    )

    val layoutNode = state.nodeRef.value!!
    if (!layoutNode.needsRemeasure && layoutNode.owner != null) {
        state.recompose()
    }
}

private class ComposableLayoutState {
    val nodeRef = Ref<LayoutNode>()

    lateinit var compositionRef: CompositionReference
    lateinit var context: Context
    lateinit var children: @Composable () -> Unit
    lateinit var measureBlock: ComposableMeasureBlock

    val measureBlocks = object : LayoutNode.NoIntristicsMeasureBlocks(
        error = "Intrinsic measurements are not supported by ComposableLayout"
    ) {
        override fun measure(
            measureScope: MeasureScope,
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureScope.LayoutResult {
            return measureBlock(
                measureScope,
                constraints,
                {
                    val list = mutableListOf<LayoutNode>()
                    addLayoutChildren(nodeRef.value!!, list)
                    list
                },
                { recompose() }
            )
        }
    }

    private fun addLayoutChildren(node: ComponentNode, list: MutableList<LayoutNode>) {
        node.visitChildren { child ->
            if (child is LayoutNode) {
                list += child
            } else {
                addLayoutChildren(child, list)
            }
        }
    }

    fun recompose() {
        Compose.subcomposeInto(nodeRef.value!!, context, compositionRef, children)
    }
}
