/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import android.content.*
import android.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class FeedbackMailScreen : IntentScreen {
  companion object {
    @Provide fun screenIntentFactory(
      appConfig: AppConfig,
      email: DeveloperEmail
    ) = ScreenIntentFactory<FeedbackMailScreen> {
      Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email.value))
        putExtra(
          Intent.EXTRA_SUBJECT,
          "Feedback for ${appConfig.appName} ${appConfig.versionName}"
        )
      }
    }
  }
}

@JvmInline value class DeveloperEmail(val value: String) {
  @Provide companion object {
    @Provide val defaultDeveloperEmail = DeveloperEmail("ivianuu@gmail.com")
  }
}
