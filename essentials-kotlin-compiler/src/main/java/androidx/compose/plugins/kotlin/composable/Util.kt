package androidx.compose.plugins.kotlin.composable

import androidx.compose.plugins.kotlin.ComposableAnonymousFunctionDescriptor
import androidx.compose.plugins.kotlin.hasComposableAnnotation
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyGetterDescriptor

fun FunctionDescriptor.isComposableFunction(): Boolean {
    if (hasComposableAnnotation()) return true

    if (this is ComposableAnonymousFunctionDescriptor) return true

    if (this is PropertyGetterDescriptor &&
        correspondingProperty.hasComposableAnnotation()) return true

    if (dispatchReceiverParameter?.hasComposableAnnotation() == true ||
        dispatchReceiverParameter?.type?.hasComposableAnnotation() == true) return true

    return false
}
