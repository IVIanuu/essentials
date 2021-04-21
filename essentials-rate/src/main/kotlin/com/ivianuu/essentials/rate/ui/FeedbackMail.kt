package com.ivianuu.essentials.rate.ui

import android.content.*
import android.net.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

object FeedbackMailKey : IntentKey

@Given
fun feedbackMailKeyIntentFactory(
    @Given appContext: AppContext,
    @Given stringResource: StringResourceProvider
): KeyIntentFactory<FeedbackMailKey> = {
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("ivianuu@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Feedback for ${
            stringResource(appContext.applicationInfo.labelRes, emptyList())
        }")
    }
}
