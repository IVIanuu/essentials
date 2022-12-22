/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

@Provide object InputMethodActionId : ActionId("input_method")

context(ResourceProvider) @Provide fun inputMethodAction() = Action(
  id = InputMethodActionId,
  title = loadResource(R.string.es_action_input_method),
  icon = staticActionIcon(R.drawable.es_ic_keyboard_hide)
)

@Provide fun inputMethodActionExecutor(
  inputMethodManager: @SystemService InputMethodManager
) = ActionExecutor<InputMethodActionId> {
  inputMethodManager.showInputMethodPicker()
}
