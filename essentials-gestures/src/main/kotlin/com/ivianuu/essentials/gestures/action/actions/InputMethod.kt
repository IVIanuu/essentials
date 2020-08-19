package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardHide
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.given

@GivenAction
fun inputMethodAction() = Action(
    key = "input_method",
    title = Resources.getString(R.string.es_action_input_method),
    icon = singleActionIcon(Icons.Default.KeyboardHide),
    execute = {
        given<InputMethodManager>().showInputMethodPicker()
    }
)
