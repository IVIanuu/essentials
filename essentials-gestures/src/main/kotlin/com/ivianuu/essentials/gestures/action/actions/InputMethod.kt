package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun InputMethodModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executor: InputMethodActionExecutor ->
        Action(
key = "input_method",
title = getString(R.string.es_action_input_method),
iconProvider = SingleActionIconProvider(Icons.Default.KeyboardHide),
            executor = executor
        ) as @StringKey("input_method") Action
    }
}

@Unscoped
internal class InputMethodActionExecutor(
    private val inputMethodManager: InputMethodManager
) : ActionExecutor {
    override suspend fun invoke() {
        inputMethodManager.showInputMethodPicker()
    }
}
 */