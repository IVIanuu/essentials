/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.googlefonts.*
import androidx.datastore.core.*
import essentials.font.*
import essentials.ui.app.*
import injekt.*

@Provide val SampleFont: AppFont = GoogleFont("Roboto Mono").asFontFamily()

@Provide @Composable fun sampleColorScheme(
  pref: DataStore<SamplePrefs>
): AppColorScheme = /*defaultAppColorScheme(
  AppColors(
    pref.data.collectAsState(null).value?.color
      ?: Color.Blue
  ),
  isSystemInDarkTheme()
)*/ if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current)
else dynamicLightColorScheme(LocalContext.current)

object Strings {
  const val Title = "Title"
  const val Text = "This is a sub text"
  const val LoremIpsum =
    "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
  val LoremIpsumChain = (0..10)
    .map { LoremIpsum }
    .joinToString("\n")
}
