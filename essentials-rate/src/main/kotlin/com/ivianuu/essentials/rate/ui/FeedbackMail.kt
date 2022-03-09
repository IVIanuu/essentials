/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import android.content.*
import android.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*

object FeedbackMailKey : IntentKey

@Provide fun feedbackMailKeyIntentFactory(
  context: AppContext,
  email: DeveloperEmail,
  RP: ResourceProvider
) = KeyIntentFactory<FeedbackMailKey> {
  Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf(email.value))
    putExtra(
      Intent.EXTRA_SUBJECT,
      "Feedback for ${loadResource<String>(context.applicationInfo.labelRes)}"
    )
  }
}

@JvmInline value class DeveloperEmail(val value: String) {
  companion object {
    @Provide val defaultDeveloperEmail = DeveloperEmail("ivianuu@gmail.com")
  }
}
