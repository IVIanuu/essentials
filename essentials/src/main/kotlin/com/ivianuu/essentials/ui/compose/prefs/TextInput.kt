package com.ivianuu.essentials.ui.compose.prefs

import android.graphics.drawable.Drawable
import android.text.InputType
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
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    allowEmptyInput: Boolean = true,
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
        buildDialog = { dismiss ->
            input(
                hint = dialogHint,
                prefill = pref.get(),
                inputType = inputType,
                allowEmpty = allowEmptyInput
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