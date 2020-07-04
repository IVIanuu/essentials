package com.ivianuu.essentials.sample.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import com.ivianuu.essentials.gestures.action.ActionExecutors
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerPage
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.safeAs
import com.ivianuu.injekt.Unscoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Unscoped
class ActionsPage(
    private val actionExecutors: ActionExecutors,
    private val actionPickerPage: ActionPickerPage,
    private val navigator: Navigator,
    private val scope: @UiScope CoroutineScope
) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Actions") }) }
        ) {
            Button(
                modifier = Modifier.center(),
                onClick = {
                    scope.launch {
                        val action = navigator.push<ActionPickerResult> {
                            actionPickerPage(
                                showDefaultOption = false,
                                showNoneOption = false
                            )
                        }.safeAs<ActionPickerResult.Action>()?.actionKey ?: return@launch
                        actionExecutors.execute(action)
                    }
                }
            ) { Text("Pick action") }
        }
    }
}
