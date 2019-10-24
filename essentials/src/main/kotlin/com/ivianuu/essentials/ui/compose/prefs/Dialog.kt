package com.ivianuu.essentials.ui.compose.prefs

import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.res.stringResource
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.dialog.dialogRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.kprefs.Pref

@Composable
fun <T> DialogPreference(
    pref: Pref<T>,
    buildDialog: MaterialDialog.(dismissDialog: () -> Unit) -> Unit,
    dialogTitle: String? = null,
    dialogMessage: String? = null,
    dialogIcon: Drawable? = null,
    dialogPositiveButtonText: String? = +stringResource(R.string.es_ok),
    dialogNegativeButtonText: String? = +stringResource(R.string.es_cancel),
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("DialogPreference:${pref.key}") {
    val navigator = +inject<Navigator>()
    Preference(
        pref = pref,
        title = title,
        summary = summary,
        singleLineSummary = singleLineSummary,
        leading = leading,
        onClick = {
            navigator.push(
                dialogRoute {
                    if (dialogTitle != null) title(text = dialogTitle)
                    if (dialogMessage != null) message(text = dialogMessage)
                    if (dialogIcon != null) icon(drawable = dialogIcon)
                    if (dialogPositiveButtonText != null) positiveButton(text = dialogPositiveButtonText)
                    if (dialogNegativeButtonText != null) negativeButton(text = dialogNegativeButtonText)
                    buildDialog { navigator.pop() }
                }
            )
        },
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}