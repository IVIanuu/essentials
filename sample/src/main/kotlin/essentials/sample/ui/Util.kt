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
import com.materialkolor.*
import com.materialkolor.ktx.*
import essentials.font.*
import essentials.ui.app.*
import injekt.*

@Provide val SampleFont: AppFont = GoogleFont("Roboto Mono").asFontFamily()

@Provide @Composable fun sampleColorScheme(
  pref: DataStore<SamplePrefs>
): AppColorScheme {
  @Composable fun result(): ColorScheme {
    val isDark = isSystemInDarkTheme()
    val prefs = pref.data.collectAsState(null).value
      ?: return if (isDark) darkColorScheme().tweak(isDark) else lightColorScheme().tweak(isDark)
    if (prefs.materialYou)
      return if (isDark) dynamicDarkColorScheme(LocalContext.current).tweak(isDark)
      else dynamicLightColorScheme(LocalContext.current).tweak(isDark)

    return dynamicColorScheme(
      isDark = isDark,
      isAmoled = false,
      primary = prefs.primary,
      secondary = prefs.secondary,
      tertiary = prefs.tertiary,
      style = prefs.paletteStyle
    ).tweak(isDark)
  }

  return animateColorScheme(result())
}

fun ColorScheme.tweak(isDark: Boolean): ColorScheme {
  return copy(
    onSurface = if (isDark) onSurface.darken(1.5f) else onSurface.lighten(1.5f)
  )
}

object Strings {
  const val Title = "Title"
  const val Text = "This is a sub text"
  const val LoremIpsum =
    "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
  val LoremIpsumChain = (0..10)
    .map { LoremIpsum }
    .joinToString("\n")
}
