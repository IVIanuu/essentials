/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import android.content.Intent
import android.net.Uri
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.navigation.IntentKey
import com.ivianuu.essentials.ui.navigation.KeyIntentFactory
import com.ivianuu.injekt.Provide

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

@JvmInline value class DeveloperEmail(val value: String)

@Provide val developerEmail = DeveloperEmail("ivianuu@gmail.com")
