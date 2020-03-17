package com.ivianuu.essentials.sample.ui

import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.layout.LayoutSize
import com.ivianuu.essentials.gestures.action.ActionExecutors
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerRoute
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.safeAs
import com.ivianuu.injekt.android.ForActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val ActionsRoute = Route {
    SimpleScreen(title = "Actions") {
        val actionExecutors = inject<ActionExecutors>()
        val navigator = NavigatorAmbient.current

        Box(modifier = LayoutSize.Fill, gravity = ContentGravity.Center) {
            val activityCoroutineScope = inject<CoroutineScope>(ForActivity)
            Button(
                onClick = {
                    activityCoroutineScope.launch {
                        val action = navigator.push<ActionPickerResult>(
                            ActionPickerRoute(
                                showDefaultOption = false,
                                showNoneOption = false
                            )
                        ).safeAs<ActionPickerResult.Action>()?.actionKey ?: return@launch
                        actionExecutors.execute(action)
                    }
                }
            ) { Text("Pick action") }
        }
    }
}