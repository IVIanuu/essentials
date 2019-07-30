package com.ivianuu.essentials.ui.compose.view

import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.ViewComposition
import androidx.ui.core.currentTextStyle
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.EditText(noinline block: ViewDsl<AppCompatEditText>.() -> Unit) =
    EditText(sourceLocation(), block)

fun ViewComposition.EditText(key: Any, block: ViewDsl<AppCompatEditText>.() -> Unit) =
    View(key, { AppCompatEditText(it) }) {
        textStyle(+currentTextStyle())
        block()
    }

fun <T : AppCompatEditText> ViewDsl<T>.inputType(inputType: KeyboardType) {
    set(inputType) {
        this.inputType = when (inputType) {
            KeyboardType.Text -> InputType.TYPE_CLASS_TEXT
            KeyboardType.Ascii -> InputType.TYPE_CLASS_TEXT // todo not complete
            KeyboardType.Number -> InputType.TYPE_CLASS_NUMBER
            KeyboardType.Phone -> InputType.TYPE_CLASS_PHONE
            KeyboardType.Uri -> InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_URI
            KeyboardType.Email -> InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            KeyboardType.Password -> InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
            KeyboardType.NumberPassword -> InputType.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        }
    }
}

fun <T : AppCompatEditText> ViewDsl<T>.imeAction(action: ImeAction) {
    set(action) {
        this.imeOptions = when (action) {
            ImeAction.Unspecified -> EditorInfo.IME_ACTION_UNSPECIFIED
            ImeAction.NoAction -> EditorInfo.IME_ACTION_NONE
            ImeAction.Go -> EditorInfo.IME_ACTION_GO
            ImeAction.Next -> EditorInfo.IME_ACTION_NEXT
            ImeAction.Previous -> EditorInfo.IME_ACTION_PREVIOUS
            ImeAction.Search -> EditorInfo.IME_ACTION_SEARCH
            ImeAction.Send -> EditorInfo.IME_ACTION_SEND
            ImeAction.Done -> EditorInfo.IME_ACTION_DONE
        }
    }
}