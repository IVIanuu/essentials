package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.graphics.Image
import androidx.ui.material.AppBarIcon
import androidx.ui.material.TopAppBar
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.image.drawableImageResource
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator

fun EsTopAppBar(title: String) {
    EsTopAppBar(title = { Text(title) })
}

fun EsTopAppBar(
    title: @Composable() () -> Unit
) = composable("EsTopAppBar") {
    val navigator = +inject<Navigator>()
    val route = +ambient(RouteAmbient)

    val navigationIconComposable: @Composable (() -> Unit)? =
        if (navigator.backStack.indexOf(route) > 0) {
            { EsNavigationIcon() }
        } else {
            null
        }

    TopAppBar(
        title = title,
        navigationIcon = navigationIconComposable
    )
}

fun EsNavigationIcon(
    icon: Image = +drawableImageResource(R.drawable.abc_ic_ab_back_material)
) = composable("EsNavigationIcon") {
    val navigator = +inject<Navigator>()
    AppBarIcon(
        icon = icon,
        onClick = { navigator.pop() }
    )
}