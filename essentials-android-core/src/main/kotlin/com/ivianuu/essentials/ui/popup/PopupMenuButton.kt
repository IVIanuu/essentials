package com.ivianuu.essentials.ui.popup

import androidx.compose.Composable
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.boundsInRoot
import androidx.ui.core.onPositioned
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Stack
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.unit.IntPxBounds
import androidx.ui.unit.round
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

@Composable
fun PopupMenuButton(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    popupStyle: PopupStyle = PopupStyleAmbient.current
) {
    PopupMenuButton(items = items, onCancel = onCancel, popupStyle = popupStyle) {
        Icon(Icons.Default.MoreVert)
    }
}

@Composable
fun PopupMenuButton(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    children: @Composable () -> Unit
) {
    val navigator = NavigatorAmbient.current

    val coordinatesHolder =
        holder<LayoutCoordinates?> { null }

    Stack(modifier = onPositioned { coordinatesHolder.value = it }) {
        Clickable(onClick = {
            navigator.push(
                PopupRoute(
                    position = coordinatesHolder.value!!.boundsInRoot.let {
                        IntPxBounds(
                            left = it.left.round(),
                            top = it.top.round(),
                            right = it.right.round(),
                            bottom = it.bottom.round()
                        )
                    },
                    onCancel = onCancel
                ) {
                    PopupMenu(
                        items = items,
                        style = popupStyle
                    )
                }
            )
        }, children = children)
    }
}