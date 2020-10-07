package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.gestures.action.executeAction
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerPage
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.launch

@FunBinding
@Composable
fun ActionsPage(
    actionPickerPage: ActionPickerPage,
    executeAction: executeAction,
    navigator: Navigator,
    uiScope: UiScope,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Actions") }) }
    ) {
        Button(
            modifier = Modifier.center(),
            onClick = {
                uiScope.launch {
                    val action = navigator.push<ActionPickerResult> {
                        actionPickerPage(false, false)
                    }
                        ?.let { it as? ActionPickerResult.Action }
                        ?.actionKey ?: return@launch

                    executeAction(action)
                }
            }
        ) { Text("Pick action") }
    }
}
