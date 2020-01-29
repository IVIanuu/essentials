package com.ivianuu.essentials.ui.popup

import androidx.compose.Composable
import androidx.ui.core.Alignment
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.MoreVert
import com.ivianuu.essentials.ui.material.IconButton
import com.ivianuu.essentials.ui.painter.Renderable
import com.ivianuu.essentials.ui.painter.VectorRenderable

@Composable
fun PopupMenuButton(
    alignment: Alignment = Alignment.TopLeft,
    image: Renderable = VectorRenderable(Icons.Default.MoreVert),
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    onCancel: (() -> Unit)? = null,
    items: List<PopupMenu.Item>
) {
    PopupTrigger(
        alignment = alignment,
        onCancel = onCancel,
        popup = {
            PopupMenu(
                items = items,
                style = popupStyle
            )
        },
        child = { showPopup ->
            IconButton(
                image = image,
                onClick = showPopup
            )
        }
    )
}

@Composable
fun <T> PopupMenuButton(
    alignment: Alignment = Alignment.TopLeft,
    image: Renderable = VectorRenderable(Icons.Default.MoreVert),
    popupStyle: PopupStyle = PopupStyleAmbient.current,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    selectedItem: T? = null,
    item: @Composable (T, Boolean) -> Unit
) {
    PopupTrigger(
        alignment = alignment,
        onCancel = onCancel,
        popup = {
            PopupMenu(
                items = items,
                selectedItem = selectedItem,
                onSelected = onSelected,
                item = item,
                style = popupStyle
            )
        },
        child = { showPopup ->
            IconButton(
                image = image,
                onClick = showPopup
            )
        }
    )
}