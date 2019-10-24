package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.graphics.Image
import androidx.ui.material.AppBarIcon
import androidx.ui.material.TopAppBar
import androidx.ui.material.surface.CurrentBackground
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.image.iconColorForBackground
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.Navigator

@Composable
fun EsTopAppBar(title: String) = composable("EsTopAppBar2") {
    EsTopAppBar(title = { Text(title) })
}

@Composable
fun EsTopAppBar(
    title: @Composable() () -> Unit,
    leading: (@Composable() () -> Unit)? = null,
    trailing: (@Composable() () -> Unit)? = null
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
        navigationIcon = leading ?: navigationIconComposable,
        actionData = listOfNotNull(trailing),
        action = { it() }
    )
}

@Composable
fun EsNavigationIcon(
    icon: Image = +drawableResource(
        R.drawable.abc_ic_ab_back_material, +iconColorForBackground(
            +ambient(
                CurrentBackground
            )
        )
    )
) = composable("EsNavigationIcon") {
    val navigator = +inject<Navigator>()
    AppBarIcon(
        icon = icon,
        onClick = { navigator.pop() }
    )
}

@Composable
fun <T> PopupMenuAppBarIcon(
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit,
    icon: Image = +drawableResource(
        R.drawable.abc_ic_menu_overflow_material, +iconColorForBackground(
            +ambient(
                CurrentBackground
            )
        )
    )
) = composable("MenuAppBarIcon") {
    PopupMenuTrigger(
        alignment = Alignment.TopRight,
        onCancel = onCancel,
        items = items,
        onSelected = onSelected,
        item = item
    ) { showPopup ->
        AppBarIcon(
            icon = icon,
            onClick = showPopup
        )
    }
}