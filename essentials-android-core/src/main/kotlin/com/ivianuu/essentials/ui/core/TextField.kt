package com.ivianuu.essentials.ui.core

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.currentTextStyle
import androidx.ui.graphics.Color
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.VisualTransformation
import androidx.ui.text.SoftwareKeyboardController
import androidx.ui.text.TextLayoutResult
import androidx.ui.text.TextRange
import androidx.ui.text.TextStyle

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unset,
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onFocusChange: (Boolean) -> Unit = {},
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    cursorColor: Color = contentColor()
) {
    var textFieldValue by state {
        TextFieldValue(value, TextRange(value.length, value.length))
    }
    if (textFieldValue.text != value) {
        val newSelection = TextRange(
            textFieldValue.selection.start.coerceIn(0, value.length),
            textFieldValue.selection.end.coerceIn(0, value.length)
        )
        textFieldValue = TextFieldValue(text = value, selection = newSelection)
    }

    androidx.ui.foundation.TextField(
        value = textFieldValue,
        onValueChange = {
            val previousValue = textFieldValue.text
            textFieldValue = it
            if (previousValue != it.text) {
                onValueChange(it.text)
            }
        },
        modifier = modifier,
        textColor = textColor,
        textStyle = textStyle,
        keyboardType = keyboardType,
        imeAction = imeAction,
        onFocusChange = onFocusChange,
        onImeActionPerformed = onImeActionPerformed,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        onTextInputStarted = onTextInputStarted,
        cursorColor = cursorColor
    )
}
