package com.ivianuu.essentials.ui.dialog

import android.view.View
import androidx.compose.memo
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.essentials.ui.compose.navigation.Route

fun ComponentComposition.Dialog(block: DialogDsl.() -> Unit) {
    val dialogHolder = +memo { Holder<MaterialDialog?>(null) }

    View<View> {
        val dsl = DialogDsl().apply(block)

        bindView {
            dialogHolder.value = MaterialDialog(context)
                .apply { dsl.buildDialog?.invoke(this) }
                .also { it.show() }
        }
        unbindView {
            dialogHolder.value?.dismiss()
            dialogHolder.value = null
        }
    }
}

fun ComponentComposition.DialogRoute(block: DialogDsl.() -> Unit) = Route(
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