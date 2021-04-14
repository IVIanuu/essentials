/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.navigation

import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.injekt.Given

class DefaultIntentKey(val intent: Intent) : IntentKey

fun Intent.toIntentKey() = DefaultIntentKey(this)

@Given
val defaultIntentKeyIntentFactory: KeyIntentFactory<DefaultIntentKey> = { it.intent }

data class AppInfoKey(val packageName: String) : IntentKey

@Given
val appInfoKeyIntentFactory: KeyIntentFactory<AppInfoKey> = { key ->
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        this.data = "package:${key.packageName}".toUri()
    }
}

data class AppKey(val packageName: String) : IntentKey

@Given
fun appKeyIntentFactory(
    @Given packageManager: PackageManager
): KeyIntentFactory<AppKey> = { key ->
    packageManager.getLaunchIntentForPackage(key.packageName)!!
}

data class ShareKey(val text: String) : IntentKey

@Given
val shareKeyIntentFactory: KeyIntentFactory<ShareKey> = { key ->
    Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, key.text)
        },
        ""
    )
}

data class UrlKey(val url: String) : IntentKey

@Given
val urlKeyIntentFactory: KeyIntentFactory<UrlKey> = { key ->
    Intent(Intent.ACTION_VIEW).apply { this.data = key.url.toUri() }
}
