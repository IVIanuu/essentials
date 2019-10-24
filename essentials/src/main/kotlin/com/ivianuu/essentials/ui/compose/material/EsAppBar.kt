package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.CurrentBackground
import androidx.ui.material.textColorForBackground
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.RouteAmbient
import com.ivianuu.essentials.ui.compose.core.composable
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
    image: Image = +drawableResource(R.drawable.abc_ic_ab_back_material)
) = composable("EsNavigationIcon") {
    val navigator = +inject<Navigator>()
    EsAppBarIcon(
        image = image,
        onClick = { navigator.pop() }
    )
}

@Composable
fun EsAppBarIcon(
    image: Image,
    tint: Color? = +textColorForBackground(+ambient(CurrentBackground)),
    onClick: () -> Unit
) = composable("EsAppBarIcon") {
    Container(width = ActionIconSize, height = ActionIconSize) {
        Ripple(bounded = false) {
            Clickable(onClick = onClick) {
                SimpleImage(image = image, tint = tint)
            }
        }
    }
}

private val ActionIconSize = 24.dp