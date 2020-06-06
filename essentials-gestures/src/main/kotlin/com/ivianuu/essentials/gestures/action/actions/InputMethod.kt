package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.KeyboardHide
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn

@Module
private fun InputMethodModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executor: InputMethodActionExecutor ->
        Action(
            key = "input_method",
            title = resourceProvider.getString(R.string.es_action_input_method),
            iconProvider = SingleActionIconProvider(Icons.Default.KeyboardHide),
            executor = executor
        ) as @StringKey("input_method") Action
    }
}

@Transient
internal class InputMethodActionExecutor(
    private val inputMethodManager: InputMethodManager
) : ActionExecutor {
    override suspend fun invoke() {
        inputMethodManager.showInputMethodPicker()
    }
}
