package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.stringResource

@ActionBinding
fun menuAction(
    choosePermissions: choosePermissions,
    runRootCommand: runRootCommand,
    stringResource: stringResource,
): Action = Action(
    key = "menu",
    title = stringResource(R.string.es_action_menu),
    icon = singleActionIcon(Icons.Default.MoreVert),
    permissions = choosePermissions { listOf(root) },
    execute = { runRootCommand("input keyevent 82") }
)
