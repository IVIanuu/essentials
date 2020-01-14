package com.ivianuu.essentials.sample.ui

import androidx.ui.layout.Center
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.gestures.action.ActionExecutors
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerRoute
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.safeAs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val ActionsRoute = Route {
    SimpleScreen(title = "Actions") {
        val actionExecutors = inject<ActionExecutors>()
        val navigator = navigator

        Center {
            Button(
                text = "Request",
                onClick = {
                    GlobalScope.launch {
                        val action = navigator.push<ActionPickerResult>(
                            ActionPickerRoute(
                                showDefaultOption = false,
                                showNoneOption = false
                            )
                        ).safeAs<ActionPickerResult.Action>()?.actionKey ?: return@launch
                        d { "picked action $action" }
                        actionExecutors.execute(action)
                    }
                }
            )
        }
    }
}