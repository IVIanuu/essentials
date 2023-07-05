/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package androidx.compose.material

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.ivianuu.essentials.Strings
import com.ivianuu.injekt.Inject

@Composable inline fun Text(
  textKey: Strings.Key0,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  textDecoration: TextDecoration? = null,
  textAlign: TextAlign? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  minLines: Int = 1,
  noinline onTextLayout: (TextLayoutResult) -> Unit = {},
  style: TextStyle = LocalTextStyle.current,
  @Inject strings: Strings,
) {
  Text(
    strings[textKey], modifier, color, fontSize, fontStyle, fontWeight,
    fontFamily, letterSpacing, textDecoration, textAlign, lineHeight, overflow,
    softWrap, maxLines, minLines, onTextLayout, style
  )
}

@Composable inline fun Text(
  textResId: Int,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  textDecoration: TextDecoration? = null,
  textAlign: TextAlign? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  minLines: Int = 1,
  noinline onTextLayout: (TextLayoutResult) -> Unit = {},
  style: TextStyle = LocalTextStyle.current
) {
  Text(
    stringResource(textResId), modifier, color, fontSize, fontStyle, fontWeight,
    fontFamily, letterSpacing, textDecoration, textAlign, lineHeight, overflow,
    softWrap, maxLines, minLines, onTextLayout, style
  )
}

@Composable inline fun Text(
  textResId: Int,
  vararg textFormatArgs: Any,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  textDecoration: TextDecoration? = null,
  textAlign: TextAlign? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  minLines: Int = 1,
  noinline onTextLayout: (TextLayoutResult) -> Unit = {},
  style: TextStyle = LocalTextStyle.current
) {
  Text(
    stringResource(textResId, *textFormatArgs), modifier, color, fontSize, fontStyle, fontWeight,
    fontFamily, letterSpacing, textDecoration, textAlign, lineHeight, overflow,
    softWrap, maxLines, minLines, onTextLayout, style
  )
}
