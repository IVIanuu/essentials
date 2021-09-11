/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  rp: ResourceProvider
): KeyIntentFactory<FeedbackMailKey> = {
  Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    putExtra(
      Intent.EXTRA_SUBJECT,
      "Feedback for ${loadResource<String>(context.applicationInfo.labelRes)}"
    )
  }
}

typealias DeveloperEmail = String

@Provide val developerEmail: DeveloperEmail = "ivianuu@gmail.com"
