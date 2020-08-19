package com.ivianuu.essentials.ui.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.currentTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.state
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.InternalTextApi
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.constrain
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unset,
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onImeActionPerformed: (ImeAction) -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    cursorColor: Color = contentColor()
) {
    var textFieldValue by state { TextFieldValue() }
    if (textFieldValue.text != value) {
        @OptIn(InternalTextApi::class)
        textFieldValue = TextFieldValue(
            text = value,
            selection = textFieldValue.selection.constrain(0, value.length)
        )
    }
    androidx.compose.foundation.BaseTextField(
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
        onImeActionPerformed = onImeActionPerformed,
        onFocusChanged = onFocusChanged,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        onTextInputStarted = onTextInputStarted,
        cursorColor = cursorColor
    )
}
