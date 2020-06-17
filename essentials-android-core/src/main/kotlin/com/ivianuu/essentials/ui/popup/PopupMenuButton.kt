package com.ivianuu.essentials.ui.popup

import androidx.compose.Composable
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.Modifier
import androidx.ui.core.boundsInRoot
import androidx.ui.core.composed
import androidx.ui.core.onPositioned
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Indication
import androidx.ui.foundation.IndicationAmbient
import androidx.ui.foundation.clickable
import androidx.ui.layout.size
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.material.ripple.RippleIndication
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.untrackedState
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

@Composable
fun PopupMenuButton(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    popupStyle: PopupStyle = PopupStyleAmbient.currentOrElse { DefaultPopupStyle() }
) {
    Box(
        modifier = Modifier
            .size(size = 40.dp)
            .popupClickable(
                items = items,
                onCancel = onCancel,
                popupStyle = popupStyle,
                indicationFactory = { RippleIndication(bounded = false) }
            )
            .plus(modifier),
        gravity = ContentGravity.Center
    ) {
        Icon(Icons.Default.MoreVert)
    }
}

@Composable
fun Modifier.popupClickable(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    popupStyle: PopupStyle = PopupStyleAmbient.currentOrElse { DefaultPopupStyle() },
    indicationFactory: @Composable () -> Indication = IndicationAmbient.current,
) = composed {
    val navigator = NavigatorAmbient.current

    val coordinates =
        untrackedState<LayoutCoordinates?> { null }

    onPositioned { coordinates.value = it }
        .clickable(indication = indicationFactory()) {
            navigator.push(
                PopupRoute(
                    position = coordinates.value!!.boundsInRoot,
                    onCancel = onCancel
                ) {
                    PopupMenu(
                        items = items,
                        style = popupStyle
                    )
                }
            )
        }
}
