package essentials.font

import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.googlefonts.*
import essentials.android.R
import essentials.ui.app.*
import injekt.*

@Provide val GoogleFontProvider = GoogleFont.Provider(
  providerAuthority = "com.google.android.gms.fonts",
  providerPackage = "com.google.android.gms",
  certificates = R.array.com_google_android_gms_fonts_certs
)

fun GoogleFont.asFontFamily(provider: GoogleFont.Provider = inject) = FontFamily(
  Font(this, provider, FontWeight.Light, FontStyle.Normal),
  Font(this, provider, FontWeight.Light, FontStyle.Italic),
  Font(this, provider, FontWeight.Normal, FontStyle.Normal),
  Font(this, provider, FontWeight.Normal, FontStyle.Italic),
  Font(this, provider, FontWeight.Medium, FontStyle.Normal),
  Font(this, provider, FontWeight.Medium, FontStyle.Italic),
  Font(this, provider, FontWeight.Bold, FontStyle.Normal),
  Font(this, provider, FontWeight.Bold, FontStyle.Italic),
  Font(this, provider, FontWeight.Black, FontStyle.Normal),
  Font(this, provider, FontWeight.Black, FontStyle.Italic),
)

@Provide fun DefaultAppFont(provider: GoogleFont.Provider = inject): AppFont =
  GoogleFont("Rubik").asFontFamily()
