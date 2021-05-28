package com.ivianuu.essentials.rate.ui

import android.content.*
import android.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

object FeedbackMailKey : IntentKey

@Provide fun feedbackMailKeyIntentFactory(
  context: AppContext,
  rp: ResourceProvider
): KeyIntentFactory<FeedbackMailKey> = {
  Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf("ivianuu@gmail.com"))
    putExtra(
      Intent.EXTRA_SUBJECT,
      "Feedback for ${loadResource<String>(context.applicationInfo.labelRes)}"
    )
  }
}
