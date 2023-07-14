/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import android.content.Intent
import android.net.Uri
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.ui.navigation.IntentScreen
import com.ivianuu.essentials.ui.navigation.ScreenIntentFactory
import com.ivianuu.injekt.Provide

object FeedbackMailScreen : IntentScreen

@Provide fun feedbackMailKeyIntentFactory(
  context: AppContext,
  email: DeveloperEmail,
  resources: Resources
) = ScreenIntentFactory<FeedbackMailScreen> {
  Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf(email.value))
    putExtra(
      Intent.EXTRA_SUBJECT,
      "Feedback for ${resources<String>(context.applicationInfo.labelRes)}"
    )
  }
}

@JvmInline value class DeveloperEmail(val value: String) {
  @Provide companion object {
    @Provide val defaultDeveloperEmail = DeveloperEmail("ivianuu@gmail.com")
  }
}
