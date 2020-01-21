package androidx.compose.plugins.kotlin.analysis

import androidx.compose.plugins.kotlin.ComposableAnnotationChecker
import androidx.compose.plugins.kotlin.ComposableFunctionDescriptor
import androidx.compose.plugins.kotlin.ComposablePropertyDescriptor
import androidx.compose.plugins.kotlin.ComposerMetadata
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.ir.declarations.IrAttributeContainer
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.util.slicedMap.BasicWritableSlice
import org.jetbrains.kotlin.util.slicedMap.RewritePolicy
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

object ComposeWritableSlices {
    val COMPOSABLE_ANALYSIS: WritableSlice<KtElement, ComposableAnnotationChecker.Composability> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val FCS_RESOLVEDCALL_COMPOSABLE: WritableSlice<KtElement, Boolean> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val INFERRED_COMPOSABLE_DESCRIPTOR: WritableSlice<FunctionDescriptor, Boolean> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val RESTART_COMPOSER_NEEDED: WritableSlice<SimpleFunctionDescriptor, Boolean> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val RESTART_COMPOSER: WritableSlice<SimpleFunctionDescriptor, ResolvedCall<*>> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val COMPOSER_METADATA: WritableSlice<VariableDescriptor, ComposerMetadata> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val IGNORE_COMPOSABLE_INTERCEPTION: WritableSlice<Call, Boolean> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val COMPOSABLE_FUNCTION_DESCRIPTOR: WritableSlice<IrAttributeContainer,
            ComposableFunctionDescriptor> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
    val COMPOSABLE_PROPERTY_DESCRIPTOR: WritableSlice<IrAttributeContainer,
            ComposablePropertyDescriptor> =
        BasicWritableSlice(RewritePolicy.DO_NOTHING)
}
