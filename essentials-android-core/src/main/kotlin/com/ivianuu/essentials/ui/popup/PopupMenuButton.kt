package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationAmbient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.onPositioned
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.common.rememberUntrackedState
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

@Composable
fun PopupMenuButton(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(size = 40.dp)
            .popupClickable(
                items = items,
                onCancel = onCancel,
                indicationFactory = { RippleIndication(bounded = false) }
            )
            .then(modifier),
        gravity = ContentGravity.Center
    ) {
        Icon(Icons.Default.MoreVert)
    }
}

@Composable
fun Modifier.popupClickable(
    items: List<PopupMenu.Item>,
    onCancel: (() -> Unit)? = null,
    indicationFactory: @Composable () -> Indication = IndicationAmbient.current,
) = composed {
    val navigator = NavigatorAmbient.current

    var coordinates by rememberUntrackedState<LayoutCoordinates?> { null }

    onPositioned { coordinates = it }
        .clickable(indication = indicationFactory()) {
            navigator.push(
                PopupRoute(
                    position = coordinates!!.boundsInRoot,
                    onCancel = onCancel
                ) {
                    PopupMenu(items = items)
                }
            )
        }
}
