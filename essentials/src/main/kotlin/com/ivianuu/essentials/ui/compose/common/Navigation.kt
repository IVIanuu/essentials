package com.ivianuu.essentials.ui.compose.common

import androidx.compose.effectOf
import androidx.compose.memo
import com.ivianuu.essentials.ui.common.urlRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route

fun navigateOnClick(route: () -> Route) = effectOf<() -> Unit> {
    val navigator = +inject<Navigator>()
    return@effectOf +memo { { navigator.push(route()) } }
}

fun openUrlOnClick(url: () -> String) = navigateOnClick { urlRoute(url()) }