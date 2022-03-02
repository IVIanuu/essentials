/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

@Provide object InputMethodActionId : ActionId("input_method")

@Provide fun inputMethodAction(RP: ResourceProvider) = Action(
  id = InputMethodActionId,
  title = loadResource(R.string.es_action_input_method),
  icon = staticActionIcon(R.drawable.es_ic_keyboard_hide)
)

@Provide fun inputMethodActionExecutor(
  inputMethodManager: @SystemService InputMethodManager
) = ActionExecutor<InputMethodActionId> {
  inputMethodManager.showInputMethodPicker()
}
