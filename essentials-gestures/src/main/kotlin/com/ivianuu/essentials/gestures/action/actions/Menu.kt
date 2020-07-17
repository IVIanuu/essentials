package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@BindAction
@Reader
fun menuAction() = Action(
    key = "menu",
    title = Resources.getString(R.string.es_action_menu),
    iconProvider = SingleActionIconProvider(Icons.Default.MoreVert),
    permissions = permissions { listOf(root) },
    executor = given<(String) -> RootActionExecutor>()("input keyevent 82")
)
