/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rubik

import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.googlefonts.*
import com.ivianuu.essentials.font.*
import com.ivianuu.essentials.ui.app.*
import injekt.*

private val googleFont = GoogleFont("Rubik")
@Provide val Rubik: AppFont = FontFamily(
  Font(googleFont, GoogleFontProvider, FontWeight.Light, FontStyle.Normal),
  Font(googleFont, GoogleFontProvider, FontWeight.Light, FontStyle.Italic),
  Font(googleFont, GoogleFontProvider, FontWeight.Normal, FontStyle.Normal),
  Font(googleFont, GoogleFontProvider, FontWeight.Normal, FontStyle.Italic),
  Font(googleFont, GoogleFontProvider, FontWeight.Medium, FontStyle.Normal),
  Font(googleFont, GoogleFontProvider, FontWeight.Medium, FontStyle.Italic),
  Font(googleFont, GoogleFontProvider, FontWeight.Bold, FontStyle.Normal),
  Font(googleFont, GoogleFontProvider, FontWeight.Bold, FontStyle.Italic),
  Font(googleFont, GoogleFontProvider, FontWeight.Black, FontStyle.Normal),
  Font(googleFont, GoogleFontProvider, FontWeight.Black, FontStyle.Italic),
)
