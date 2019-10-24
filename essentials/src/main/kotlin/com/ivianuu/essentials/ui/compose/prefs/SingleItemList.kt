package com.ivianuu.essentials.ui.compose.prefs

import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.res.stringResource
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun SingleItemListPreference(
    pref: Pref<String>,
    entries: List<SingleItemListPreference.Item>,
    dialogTitle: String? = null,
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
) = composable("MultiSelectListPreference") {
    DialogPreference(
        pref = pref,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        dialogIcon = dialogIcon,
        dialogPositiveButtonText = dialogPositiveButtonText,
        dialogNegativeButtonText = dialogNegativeButtonText,
        buildDialog = {
            val currentValue = pref.get()
            val selectedIndex = entries.indexOfFirst { it.value == currentValue }

            listItemsSingleChoice(
                initialSelection = selectedIndex,
                items = entries.map { it.title },
                waitForPositiveButton = false
            ) { dialog, position, _ ->
                val newValue = entries[position].value
                if (onChange?.invoke(newValue) != false) {
                    pref.set(newValue)
                }

                dialog.dismiss()
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

object SingleItemListPreference {
    data class Item(
        val title: String,
        val value: String
    ) {
        constructor(value: String) : this(value, value)
    }
}