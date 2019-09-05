package com.ivianuu.essentials.ui.compose

import android.app.Activity
import android.content.ClipboardManager
import android.content.Intent
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.common.Route
import com.ivianuu.essentials.ui.common.ActivityRoute
import com.ivianuu.essentials.ui.common.UrlRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.kprefs.Pref

fun ComponentComposition.openUrlOnClick(urlProvider: () -> String): () -> Unit =
    navigateOnClick { UrlRoute(urlProvider()) }

fun ComponentComposition.sendIntentOnClick(intentProvider: (Activity) -> Intent): () -> Unit =
    navigateOnClick { ActivityRoute(intentProvider) }

fun ComponentComposition.navigateOnClick(routeProvider: () -> Route): () -> Unit {
    val navigator = ambient(NavigatorAmbient)
    return { navigator.push(routeProvider()) }
}

fun ComponentComposition.copyToClipboardOnClick(textProvider: () -> String): () -> Unit {
    val clipboardManager = inject<ClipboardManager>()
    return { clipboardManager.text = textProvider() }
}

fun <T> ComponentComposition.showMultiSelectDialogOnClick(
    title: String? = null,
    titleRes: Int? = null,
    pref: Pref<Set<T>>,
    items: Array<Pair<String, T>>,
    onChangePredicate: ((Set<T>) -> Boolean)? = null
) = navigateOnClick {
    DialogRoute {
        MultiSelectListDialog(
            title = title,
            titleRes = titleRes,
            pref = pref,
            items = items,
            onChangePredicate = onChangePredicate
        )
    }
}

fun ComponentComposition.showTextInputDialogOnClick(
    title: String? = null,
    titleRes: Int? = null,
    hint: String? = null,
    hintRes: Int? = null,
    pref: Pref<String>,
    onChangePredicate: ((String) -> Boolean)? = null
) = navigateOnClick {
    DialogRoute {
        TextInputDialog(
            title = title,
            titleRes = titleRes,
            hint = hint,
            hintRes = hintRes,
            pref = pref,
            onChangePredicate = onChangePredicate
        )
    }
}