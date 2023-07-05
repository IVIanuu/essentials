package com.ivianuu.essentials.font

import androidx.compose.ui.text.googlefonts.GoogleFont

val GoogleFontProvider = GoogleFont.Provider(
  providerAuthority = "com.google.android.gms.fonts",
  providerPackage = "com.google.android.gms",
  certificates = R.array.com_google_android_gms_fonts_certs
)
