package com.ivianuu.essentials.ui.dialog

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.memo
import com.ivianuu.compose.onBindView
import com.ivianuu.compose.onUnbindView

fun ComponentComposition.Dialog(block: MaterialDialog.() -> Unit) {
    val dialogHolder = memo { Holder<MaterialDialog?>(null) }

    View<View> {
        onBindView {
            dialogHolder.value = MaterialDialog(it.context)
                .apply(block)
                .also { it.show() }
        }
        onUnbindView {
            dialogHolder.value?.dismiss()
            dialogHolder.value = null
        }
    }
}

fun DialogRoute(block: ComponentComposition.() -> Unit) = Route(
    isFloating = true
) {
    block()
}

private class Holder<T>(var value: T)