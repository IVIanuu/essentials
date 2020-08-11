package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources

@GivenAction
fun menuAction() = Action(
    key = "menu",
    title = Resources.getString(R.string.es_action_menu),
    icon = singleActionIcon(Icons.Default.MoreVert),
    permissions = permissions { listOf(root) },
    execute = { runRootCommand("input keyevent 82") }
)
