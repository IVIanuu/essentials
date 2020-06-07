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
import androidx.ui.layout.preferredSize
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.material.ripple.RippleIndication
import androidx.ui.unit.IntPxBounds
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

@Composable
fun PopupMenuButton(
    items: List<PopupMenu.Item>,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    popupStyle: PopupStyle = PopupStyleAmbient.currentOrElse { DefaultPopupStyle() }
) {
    Box(
        modifier = Modifier.preferredSize(size = 40.dp)
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

    val coordinatesHolder =
        holder<LayoutCoordinates?> { null }

    onPositioned { coordinatesHolder.value = it }
        .clickable(indication = indicationFactory()) {
            navigator.push(
                PopupRoute(
                    position = coordinatesHolder.value!!.boundsInRoot.let {
                        IntPxBounds(
                            left = it.left.toInt().ipx,
                            top = it.top.toInt().ipx,
                            right = it.right.toInt().ipx,
                            bottom = it.bottom.toInt().ipx
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
        }
}
