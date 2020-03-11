package com.ivianuu.essentials.android.ui.popup

import androidx.compose.Composable
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnPositioned
import androidx.ui.core.boundsInRoot
import androidx.ui.foundation.Icon
import androidx.ui.layout.Wrap
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.unit.IntPxBounds
import androidx.ui.unit.round
import com.ivianuu.essentials.android.ui.common.holder
import com.ivianuu.essentials.android.ui.material.IconButton
import com.ivianuu.essentials.android.ui.navigation.NavigatorAmbient

@Composable
fun PopupMenuButton(
    icon: @Composable () -> Unit = { Icon(Icons.Default.MoreVert) },
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    onCancel: (() -> Unit)? = null,
    items: List<PopupMenu.Item>
) {
    val navigator = NavigatorAmbient.current

    Wrap {
        val coordinatesHolder =
            holder<LayoutCoordinates?> { null }
        OnPositioned { coordinatesHolder.value = it }

        IconButton(
            onClick = {
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
            },
            icon = icon
        )
    }
}

@Composable
fun <T> PopupMenuButton(
    icon: @Composable () -> Unit = { Icon(Icons.Default.MoreVert) },
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    itemCallback: @Composable (T, Boolean) -> Unit,
    onSelected: (T) -> Unit,
    selectedItem: T? = null
) {
    val navigator = NavigatorAmbient.current

    Wrap {
        val coordinatesHolder =
            holder<LayoutCoordinates?> { null }
        OnPositioned { coordinatesHolder.value = it }

        IconButton(
            onClick = {
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
                            selectedItem = selectedItem,
                            onSelected = onSelected,
                            itemCallback = itemCallback,
                            style = popupStyle
                        )
                    }
                )
            },
            icon = icon
        )
    }
}
