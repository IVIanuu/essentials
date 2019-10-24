package com.ivianuu.essentials.ui.compose.prefs

import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.res.stringResource
import com.afollestad.materialdialogs.input.input
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun TextInputPreference(
    pref: Pref<String>,
    dialogTitle: String? = null,
    dialogHint: String? = null,
    dialogMessage: String? = null,
    dialogIcon: Drawable? = null,
    dialogPositiveButtonText: String? = +stringResource(R.string.es_ok),
    dialogNegativeButtonText: String? = +stringResource(R.string.es_cancel),
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((String) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("TextInputPreference") {
    DialogPreference(
        pref = pref,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        dialogIcon = dialogIcon,
        dialogPositiveButtonText = dialogPositiveButtonText,
        dialogNegativeButtonText = dialogNegativeButtonText,
        buildDialog = {
            input(
                hint = dialogHint,
                prefill = pref.get()
            ) { _, input ->
                if (onChange?.invoke(input.toString()) != false) {
                    pref.set(input.toString())
                }
            }
        },
        title = title,
        summary = summary,
        singleLineSummary = singleLineSummary,
        leading = leading,
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}