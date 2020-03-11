package com.ivianuu.essentials.sample.ui

import androidx.ui.layout.Center
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.android.ui.common.SimpleScreen
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.android.ui.material.Button
import com.ivianuu.essentials.android.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.android.ui.navigation.Route
import com.ivianuu.essentials.gestures.action.ActionExecutors
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerRoute
import com.ivianuu.essentials.util.safeAs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val ActionsRoute = Route {
    SimpleScreen(title = "Actions") {
        val actionExecutors = inject<ActionExecutors>()
        val navigator = NavigatorAmbient.current

        Center {
            Button(
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
            ) { Text("Pick action") }
        }
    }
}