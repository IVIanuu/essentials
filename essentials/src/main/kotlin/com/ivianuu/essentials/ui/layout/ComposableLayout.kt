package com.ivianuu.essentials.ui.layout

import android.content.Context
import androidx.compose.Composable
import androidx.compose.Compose
import androidx.compose.CompositionReference
import androidx.compose.composer
import androidx.compose.compositionReference
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.ui.core.Constraints
import androidx.ui.core.ContextAmbient
import androidx.ui.core.LayoutNode
import androidx.ui.core.Measurable
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import androidx.ui.core.Ref
import com.github.ajalt.timberkt.d

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

    onDispose { state.dispose() }

    composer.emit<LayoutNode>(
        key = 0,
        ctor = { LayoutNode() },
        update = {
            node.modifier = modifier
            node.ref = state.nodeRef
            node.measureBlocks = state.measureBlocks
        }
    )

    state.recompose()
}

private class ComposableLayoutState {
    val nodeRef = Ref<LayoutNode>()

    lateinit var compositionRef: CompositionReference
    lateinit var context: Context
    lateinit var children: @Composable () -> Unit
    lateinit var measureBlock: ComposableMeasureBlock

    val measureBlocks = object : LayoutNode.NoIntrinsicsMeasureBlocks(
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
                { nodeRef.value!!.layoutChildren },
                { recompose() }
            )
        }
    }

    fun recompose() {
        d { "recompose" }
        Compose.subcomposeInto(nodeRef.value!!, context, compositionRef, children)
    }

    fun dispose() {
        d { "dispose" }
        Compose.disposeComposition(nodeRef.value!!, context, compositionRef)
    }
}
