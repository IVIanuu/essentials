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