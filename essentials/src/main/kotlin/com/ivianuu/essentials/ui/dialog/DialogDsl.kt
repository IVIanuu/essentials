package com.ivianuu.essentials.ui.dialog

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.memo
import com.ivianuu.compose.onBindView
import com.ivianuu.compose.onUnbindView

fun ComponentComposition.Dialog(block: DialogDsl.() -> Unit) {
    val dialogHolder = memo { Holder<MaterialDialog?>(null) }

    View<View> {
        val dsl = DialogDsl().apply(block)

        onBindView {
            dialogHolder.value = MaterialDialog(it.context)
                .apply { dsl.buildDialog?.invoke(this) }
                .also { it.show() }
        }
        onUnbindView {
            dialogHolder.value?.dismiss()
            dialogHolder.value = null
        }
    }
}

fun DialogRoute(block: DialogDsl.() -> Unit) = Route(
    isFloating = true
) {
    Dialog(block)
}

private class Holder<T>(var value: T)

class DialogDsl {

    internal var buildDialog: (MaterialDialog.() -> Unit)? = null

    fun buildDialog(block: MaterialDialog.() -> Unit) {
        buildDialog = block
    }

}