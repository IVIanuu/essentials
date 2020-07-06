package com.ivianuu.essentials.sample.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import com.ivianuu.essentials.gestures.action.executeAction
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerPage
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.uiScope
import com.ivianuu.essentials.util.safeAs
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Reader
@Composable
fun ActionsPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Actions") }) }
    ) {
        Button(
            modifier = Modifier.center(),
            onClick = {
                uiScope.launch {
                    val action = navigator.push<ActionPickerResult> {
                        ActionPickerPage(
                            showDefaultOption = false,
                            showNoneOption = false
                        )
                    }.safeAs<ActionPickerResult.Action>()?.actionKey ?: return@launch
                    executeAction(action)
                }
            }
        ) { Text("Pick action") }
    }
}
